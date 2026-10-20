package com.terraskills.powers.exploration;

import com.terraskills.powers.TerraSkillsPowers;
import com.terraskills.powers.config.PowersConfig;
import com.terraskills.powers.skill.SkillRanks;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Records meaningful shared travel and applies Trailblazing/Well Trodden as one blended movement modifier. */
public final class TrailActivityPowers {
    private static final ResourceLocation SPEED_ID = ResourceLocation.fromNamespaceAndPath(TerraSkillsPowers.MOD_ID, "trail_activity_speed");
    private static final Map<UUID, Sample> SAMPLES = new HashMap<>();
    private static final Map<UUID, ContributionWindow> CONTRIBUTIONS = new HashMap<>();

    private TrailActivityPowers() {}

    public static void tick(ServerPlayer player) {
        int interval = Math.max(1, PowersConfig.TRAIL_ACTIVITY_SAMPLE_INTERVAL_TICKS.get());
        if (player.tickCount % interval == 0) sample(player);
        reconcile(player);
    }

    public static void reconcile(ServerPlayer player) {
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return;
        speed.removeModifier(SPEED_ID);
        if (!eligible(player)) return;

        int trail = Math.clamp(SkillRanks.trailblazing(player), 0, 5);
        int trodden = Math.clamp(SkillRanks.wellTrodden(player), 0, 5);
        if (trail == 0 && trodden == 0) return;
        double activity = smoothedActivity(player.serverLevel(), player.chunkPosition().x, player.chunkPosition().z,
                player.serverLevel().getGameTime());
        // The weights add to one. Equal ranks therefore remain a single 3% per-rank maximum rather than stacking.
        double bonus = 0.03D * trail * (1.0D - activity) + 0.03D * trodden * activity;
        if (bonus > 0.0D) speed.addOrUpdateTransientModifier(new AttributeModifier(SPEED_ID, bonus,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    }

    public static void clear(ServerPlayer player) {
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) speed.removeModifier(SPEED_ID);
        SAMPLES.remove(player.getUUID());
        CONTRIBUTIONS.remove(player.getUUID());
    }

    public static void forget(UUID playerId) {
        SAMPLES.remove(playerId);
        CONTRIBUTIONS.remove(playerId);
    }

    public static void clearAll() {
        SAMPLES.clear();
        CONTRIBUTIONS.clear();
    }

    private static void sample(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        long now = level.getGameTime();
        boolean eligible = eligible(player);
        Sample current = new Sample(level.dimension(), player.getX(), player.getY(), player.getZ(), eligible);
        Sample previous = SAMPLES.put(player.getUUID(), current);
        if (previous == null || !previous.dimension().equals(level.dimension()) || !previous.eligible() || !eligible) return;

        double dx = current.x() - previous.x();
        double dz = current.z() - previous.z();
        double displacement = Math.sqrt(dx * dx + dz * dz);
        if (!(displacement > 0.01D) || displacement > PowersConfig.TRAIL_ACTIVITY_MAX_SAMPLE_DISTANCE.get()) return;

        ContributionWindow window = CONTRIBUTIONS.computeIfAbsent(player.getUUID(), ignored -> new ContributionWindow(now));
        if (now < window.started || now - window.started >= Math.max(1L, PowersConfig.TRAIL_ACTIVITY_CONTRIBUTION_WINDOW_TICKS.get())) {
            window.started = now;
            window.byArea.clear();
        }
        contributeAlongPath(level, previous.x(), previous.z(), current.x(), current.z(), displacement, now, window);
    }

    private static void contributeAlongPath(ServerLevel level, double fromX, double fromZ, double toX, double toZ, double contribution, long now,
                                            ContributionWindow window) {
        int startX = floorChunk(fromX), startZ = floorChunk(fromZ);
        int endX = floorChunk(toX), endZ = floorChunk(toZ);
        int steps = Math.max(Math.abs(endX - startX), Math.abs(endZ - startZ)) + 1;
        AreaActivityData data = AreaActivityData.get(level);
        for (int index = 0; index < steps; index++) {
            double fraction = steps == 1 ? 0.0D : (double) index / (steps - 1);
            int chunkX = floorChunk(fromX + (toX - fromX) * fraction);
            int chunkZ = floorChunk(fromZ + (toZ - fromZ) * fraction);
            long chunk = pack(chunkX, chunkZ);
            AreaKey area = new AreaKey(level.dimension(), chunk);
            double portion = contribution / steps;
            double used = window.byArea.getOrDefault(area, 0.0D);
            double accepted = Math.min(portion, Math.max(0.0D, PowersConfig.TRAIL_ACTIVITY_CONTRIBUTION_CAP_BLOCKS.get() - used));
            if (accepted > 0.0D) {
                data.add(chunk, accepted, now);
                window.byArea.put(area, used + accepted);
            }
        }
    }

    private static double smoothedActivity(ServerLevel level, int centerX, int centerZ, long now) {
        AreaActivityData data = AreaActivityData.get(level);
        double weighted = 0.0D;
        double totalWeight = 0.0D;
        for (int dz = -1; dz <= 1; dz++) for (int dx = -1; dx <= 1; dx++) {
            double weight = dx == 0 && dz == 0 ? 1.0D : (dx == 0 || dz == 0 ? 0.60D : 0.35D);
            weighted += data.activity(pack(centerX + dx, centerZ + dz), now) * weight;
            totalWeight += weight;
        }
        return AreaActivityData.normalize(weighted / totalWeight);
    }

    private static boolean eligible(ServerPlayer player) {
        return !player.isSpectator() && !player.isPassenger() && !player.isFallFlying()
                && !player.getAbilities().flying && !player.isSwimming() && !player.isInWater() && player.onGround();
    }

    private static int floorChunk(double coordinate) { return ((int) Math.floor(coordinate)) >> 4; }
    private static long pack(int x, int z) { return ((long) x << 32) ^ (z & 0xffffffffL); }

    private record Sample(ResourceKey<Level> dimension, double x, double y, double z, boolean eligible) {}
    private record AreaKey(ResourceKey<Level> dimension, long chunk) {}

    private static final class ContributionWindow {
        private long started;
        private final Map<AreaKey, Double> byArea = new HashMap<>();

        private ContributionWindow(long started) { this.started = started; }
    }
}
