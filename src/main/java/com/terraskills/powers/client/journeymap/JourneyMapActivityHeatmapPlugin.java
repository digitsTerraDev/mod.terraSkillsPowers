package com.terraskills.powers.client.journeymap;

import com.terraskills.powers.TerraSkillsPowers;
import com.terraskills.powers.client.ClientActivityHeatmap;
import com.terraskills.powers.network.ActivityHeatmapPayload;
import com.terraskills.powers.network.ActivityHeatmapRequestPayload;
import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.IClientPlugin;
import journeymap.api.v2.client.display.Context;
import journeymap.api.v2.client.display.PolygonOverlay;
import journeymap.api.v2.client.event.DisplayUpdateEvent;
import journeymap.api.v2.client.model.MapPolygon;
import journeymap.api.v2.client.model.ShapeProperties;
import journeymap.api.v2.client.util.UIState;
import journeymap.api.v2.common.JourneyMapPlugin;
import journeymap.api.v2.common.event.ClientEventRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/** Optional JourneyMap renderer for the server's shared, dimension-local trail activity. */
@SuppressWarnings("deprecation")
@JourneyMapPlugin(apiVersion = IClientAPI.API_VERSION, dependencies = TerraSkillsPowers.MOD_ID)
public final class JourneyMapActivityHeatmapPlugin implements IClientPlugin {
    private static final int CLIENT_REQUEST_INTERVAL_TICKS = 20;
    private static final String OVERLAY_GROUP = "TerraSkills: Shared Activity";

    private final Map<Context.UI, View> activeViews = new EnumMap<>(Context.UI.class);
    private final Map<ChunkKey, PolygonOverlay> overlays = new HashMap<>();
    private IClientAPI api;
    private long lastAppliedRevision = -1L;
    private int requestSequence;
    private int lastRequestTick = -CLIENT_REQUEST_INTERVAL_TICKS;
    private int lastClientTick = -1;

    @Override
    public String getModId() {
        return TerraSkillsPowers.MOD_ID;
    }

    @Override
    public void initialize(IClientAPI jmClientApi) {
        api = jmClientApi;
        ClientEventRegistry.DISPLAY_UPDATE_EVENT.subscribe(TerraSkillsPowers.MOD_ID, this::onDisplayUpdate);
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> onClientTick());
    }

    private void onDisplayUpdate(DisplayUpdateEvent event) {
        UIState state = event.uiState;
        View view = View.from(state);
        if (view == null) {
            activeViews.remove(state.ui);
        } else {
            activeViews.put(state.ui, view);
        }
        requestActiveView();
    }

    private void onClientTick() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            activeViews.clear();
            lastClientTick = -1;
        } else {
            if (lastClientTick >= 0 && minecraft.player.tickCount < lastClientTick) {
                lastRequestTick = -CLIENT_REQUEST_INTERVAL_TICKS;
            }
            lastClientTick = minecraft.player.tickCount;
            if (activeViews.isEmpty() || minecraft.player.tickCount % CLIENT_REQUEST_INTERVAL_TICKS == 0) {
                refreshActiveViews();
            }
            requestActiveView();
        }
        applyLatestSnapshot();
    }

    private void refreshActiveViews() {
        for (Context.UI ui : Context.UI.all()) {
            UIState state = api.getUIState(ui);
            View view = state == null ? null : View.from(state);
            if (view == null) {
                activeViews.remove(ui);
            } else {
                activeViews.put(ui, view);
            }
        }
    }

    private void requestActiveView() {
        if (!ClientActivityHeatmap.enabled()) return;
        View view = preferredView();
        Minecraft minecraft = Minecraft.getInstance();
        if (view == null || minecraft.player == null || minecraft.level == null
                || !minecraft.level.dimension().location().equals(view.dimension())) return;

        int tick = minecraft.player.tickCount;
        if (tick - lastRequestTick < CLIENT_REQUEST_INTERVAL_TICKS) return;
        lastRequestTick = tick;
        PacketDistributor.sendToServer(new ActivityHeatmapRequestPayload(++requestSequence, view.dimension(),
                view.centerChunkX(), view.centerChunkZ(), view.radiusChunks()));
    }

    private View preferredView() {
        View fullscreen = activeViews.get(Context.UI.Fullscreen);
        if (fullscreen != null) return fullscreen;
        View webmap = activeViews.get(Context.UI.Webmap);
        return webmap != null ? webmap : activeViews.get(Context.UI.Minimap);
    }

    private void applyLatestSnapshot() {
        ClientActivityHeatmap.State current = ClientActivityHeatmap.state();
        if (current.revision() == lastAppliedRevision) return;
        lastAppliedRevision = current.revision();
        if (current.snapshot() == null) {
            clearOverlays();
            return;
        }

        ClientActivityHeatmap.Snapshot snapshot = current.snapshot();
        ResourceKey<Level> dimension = dimensionKey(snapshot.dimension());
        Set<ChunkKey> received = new HashSet<>();
        for (ActivityHeatmapPayload.ChunkActivity entry : snapshot.entries()) {
            if (!Float.isFinite(entry.activity()) || !inside(snapshot, entry.chunkX(), entry.chunkZ())) continue;
            received.add(new ChunkKey(dimension, entry.chunkX(), entry.chunkZ()));
        }

        Iterator<Map.Entry<ChunkKey, PolygonOverlay>> existing = overlays.entrySet().iterator();
        while (existing.hasNext()) {
            Map.Entry<ChunkKey, PolygonOverlay> entry = existing.next();
            ChunkKey key = entry.getKey();
            if (!key.dimension().equals(dimension) || !inside(snapshot, key.chunkX(), key.chunkZ()) || !received.contains(key)) {
                api.remove(entry.getValue());
                existing.remove();
            }
        }

        for (ActivityHeatmapPayload.ChunkActivity activity : snapshot.entries()) {
            if (!Float.isFinite(activity.activity()) || !inside(snapshot, activity.chunkX(), activity.chunkZ())) continue;
            ChunkKey key = new ChunkKey(dimension, activity.chunkX(), activity.chunkZ());
            float normalized = clamp(activity.activity());
            PolygonOverlay overlay = overlays.get(key);
            if (overlay == null) {
                overlay = newOverlay(key, normalized);
                overlays.put(key, overlay);
            } else {
                overlay.setShapeProperties(shape(normalized));
                overlay.setTitle(title(normalized));
            }
            try {
                api.show(overlay);
            } catch (Exception exception) {
                TerraSkillsPowers.LOGGER.warn("Unable to update JourneyMap activity heatmap overlay", exception);
            }
        }
    }

    private PolygonOverlay newOverlay(ChunkKey key, float activity) {
        int minX = key.chunkX() << 4;
        int minZ = key.chunkZ() << 4;
        int maxX = minX + 16;
        int maxZ = minZ + 16;
        MapPolygon polygon = new MapPolygon(
                new BlockPos(maxX, 0, maxZ), new BlockPos(maxX, 0, minZ),
                new BlockPos(minX, 0, minZ), new BlockPos(minX, 0, maxZ));
        PolygonOverlay overlay = new PolygonOverlay(TerraSkillsPowers.MOD_ID, key.dimension(), shape(activity), polygon);
        overlay.setOverlayGroupName(OVERLAY_GROUP);
        overlay.setTitle(title(activity));
        overlay.setActiveUIs(Context.UI.all());
        overlay.setActiveMapTypes(Context.MapType.all());
        overlay.setDisplayOrder(100);
        return overlay;
    }

    private void clearOverlays() {
        for (PolygonOverlay overlay : overlays.values()) api.remove(overlay);
        overlays.clear();
    }

    private static ShapeProperties shape(float activity) {
        int color = color(activity);
        return new ShapeProperties()
                .setFillColor(color)
                .setFillOpacity(0.12F + 0.43F * activity)
                .setStrokeColor(color)
                .setStrokeOpacity(0.0F)
                .setStrokeWidth(0.0F);
    }

    private static String title(float activity) {
        return "Shared activity: " + Math.round(activity * 100.0F) + "%";
    }

    private static int color(float activity) {
        return activity <= 0.5F
                ? blend(0x2C7BB6, 0xFEE08B, activity * 2.0F)
                : blend(0xFEE08B, 0xD7191C, (activity - 0.5F) * 2.0F);
    }

    private static int blend(int from, int to, float amount) {
        float clamped = clamp(amount);
        int red = Math.round(((from >>> 16) & 0xFF) + (((to >>> 16) & 0xFF) - ((from >>> 16) & 0xFF)) * clamped);
        int green = Math.round(((from >>> 8) & 0xFF) + (((to >>> 8) & 0xFF) - ((from >>> 8) & 0xFF)) * clamped);
        int blue = Math.round((from & 0xFF) + ((to & 0xFF) - (from & 0xFF)) * clamped);
        return (red << 16) | (green << 8) | blue;
    }

    private static float clamp(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }

    private static boolean inside(ClientActivityHeatmap.Snapshot snapshot, int chunkX, int chunkZ) {
        return chunkX >= snapshot.minChunkX() && chunkX <= snapshot.maxChunkX()
                && chunkZ >= snapshot.minChunkZ() && chunkZ <= snapshot.maxChunkZ();
    }

    private static ResourceKey<Level> dimensionKey(ResourceLocation id) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null && minecraft.level.dimension().location().equals(id)) return minecraft.level.dimension();
        return ResourceKey.create(Registries.DIMENSION, id);
    }

    private record ChunkKey(ResourceKey<Level> dimension, int chunkX, int chunkZ) {}

    private record View(ResourceLocation dimension, int centerChunkX, int centerChunkZ, int radiusChunks) {
        private static View from(UIState state) {
            if (!state.active || state.mapCenter == null || state.blockBounds == null) return null;
            AABB bounds = state.blockBounds;
            int centerChunkX = blockToChunk(state.mapCenter.getX());
            int centerChunkZ = blockToChunk(state.mapCenter.getZ());
            int minChunkX = blockToChunk(bounds.minX);
            int minChunkZ = blockToChunk(bounds.minZ);
            int maxChunkX = blockToChunk(bounds.maxX);
            int maxChunkZ = blockToChunk(bounds.maxZ);
            int radius = Math.max(Math.max(Math.abs(centerChunkX - minChunkX), Math.abs(maxChunkX - centerChunkX)),
                    Math.max(Math.abs(centerChunkZ - minChunkZ), Math.abs(maxChunkZ - centerChunkZ)));
            return new View(state.dimension.location(), centerChunkX, centerChunkZ, Math.max(1, radius));
        }

        private static int blockToChunk(double block) {
            return ((int) Math.floor(block)) >> 4;
        }

        private static int blockToChunk(int block) {
            return block >> 4;
        }
    }
}
