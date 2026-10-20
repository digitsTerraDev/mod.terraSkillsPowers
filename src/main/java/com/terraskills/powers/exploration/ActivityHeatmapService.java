package com.terraskills.powers.exploration;

import com.terraskills.powers.config.PowersConfig;
import com.terraskills.powers.network.ActivityHeatmapControlPayload;
import com.terraskills.powers.network.ActivityHeatmapPayload;
import com.terraskills.powers.network.ActivityHeatmapRequestPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Bounded, request-driven server bridge between SavedData activity and the optional JourneyMap client renderer. */
public final class ActivityHeatmapService {
    private static final Map<UUID, Long> LAST_REQUEST_TICKS = new HashMap<>();
    private static final Set<UUID> ENABLED_PLAYERS = new HashSet<>();

    private ActivityHeatmapService() {}

    public static void handleRequest(ServerPlayer player, ActivityHeatmapRequestPayload request) {
        if (!ENABLED_PLAYERS.contains(player.getUUID())) return;
        ServerLevel level = player.serverLevel();
        // A map client may ask only for the dimension the player currently occupies.
        if (!level.dimension().location().equals(request.dimension())) return;

        long now = level.getGameTime();
        Long previous = LAST_REQUEST_TICKS.get(player.getUUID());
        long cooldown = Math.max(1L, PowersConfig.TRAIL_ACTIVITY_HEATMAP_REQUEST_COOLDOWN_TICKS.get());
        if (previous != null && now >= previous && now - previous < cooldown) return;
        LAST_REQUEST_TICKS.put(player.getUUID(), now);

        int radius = Math.clamp(request.radiusChunks(), 1, PowersConfig.TRAIL_ACTIVITY_HEATMAP_MAX_RADIUS_CHUNKS.get());
        int minChunkX = saturatingAdd(request.centerChunkX(), -radius);
        int maxChunkX = saturatingAdd(request.centerChunkX(), radius);
        int minChunkZ = saturatingAdd(request.centerChunkZ(), -radius);
        int maxChunkZ = saturatingAdd(request.centerChunkZ(), radius);
        List<ActivityHeatmapPayload.ChunkActivity> entries = AreaActivityData.get(level)
                .heatmap(minChunkX, minChunkZ, maxChunkX, maxChunkZ, now,
                        PowersConfig.TRAIL_ACTIVITY_HEATMAP_MAX_ENTRIES.get())
                .stream()
                .map(entry -> new ActivityHeatmapPayload.ChunkActivity(entry.chunkX(), entry.chunkZ(), entry.activity()))
                .toList();
        PacketDistributor.sendToPlayer(player, new ActivityHeatmapPayload(request.requestId(), level.dimension().location(),
                minChunkX, minChunkZ, maxChunkX, maxChunkZ, entries));
    }

    /** Debug overlays are deliberately session-local and can only be enabled through the operator command. */
    public static void setEnabled(ServerPlayer player, boolean enabled) {
        UUID playerId = player.getUUID();
        if (enabled) {
            ENABLED_PLAYERS.add(playerId);
        } else {
            ENABLED_PLAYERS.remove(playerId);
        }
        LAST_REQUEST_TICKS.remove(playerId);
        PacketDistributor.sendToPlayer(player, new ActivityHeatmapControlPayload(enabled));
    }

    public static void forget(UUID playerId) {
        LAST_REQUEST_TICKS.remove(playerId);
        ENABLED_PLAYERS.remove(playerId);
    }

    public static void clearAll() {
        LAST_REQUEST_TICKS.clear();
        ENABLED_PLAYERS.clear();
    }

    private static int saturatingAdd(int value, int adjustment) {
        return (int) Math.clamp((long) value + adjustment, (long) Integer.MIN_VALUE, (long) Integer.MAX_VALUE);
    }
}
