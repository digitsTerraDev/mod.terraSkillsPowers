package com.terraskills.powers.hazard;

import com.terraskills.powers.collapse.CarefulMiningContext;
import com.terraskills.powers.config.PowersConfig;
import com.terraskills.powers.skill.SkillRanks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/** Bounded, per-collapse-front shore checks. No world scanning is performed on ticks. */
public final class HazardManagement {
    private static final ThreadLocal<Set<Long>> ATTEMPTED_SHORES = ThreadLocal.withInitial(HashSet::new);

    private HazardManagement() {}

    public static void beginCollapse() {
        ATTEMPTED_SHORES.get().clear();
    }

    public static void endCollapse() {
        ATTEMPTED_SHORES.remove();
    }

    /** @return true when the propagation front was arrested and must not turn into a falling block. */
    public static boolean tryArrest(Level level, BlockPos collapsingPos) {
        int radius = PowersConfig.HAZARD_SHORE_RADIUS.get();
        double maxDistanceSq = (double) radius * radius;
        BlockPos best = null;
        ShoreType bestType = null;
        double bestDistanceSq = Double.MAX_VALUE;
        Set<Long> attempted = ATTEMPTED_SHORES.get();

        for (BlockPos candidate : BlockPos.betweenClosed(collapsingPos.offset(-radius, -radius, -radius), collapsingPos.offset(radius, radius, radius))) {
            long key = candidate.asLong();
            if (attempted.contains(key)) continue;
            BlockState state = level.getBlockState(candidate);
            ShoreType type = ShoreType.from(state);
            if (type == null) continue;
            double distanceSq = candidate.distSqr(collapsingPos);
            if (distanceSq <= maxDistanceSq && distanceSq < bestDistanceSq) {
                best = candidate.immutable();
                bestType = type;
                bestDistanceSq = distanceSq;
            }
        }
        if (best == null) return false;

        attempted.add(best.asLong());
        level.destroyBlock(best, false); // A shore is consumed whether its one attempt succeeds or fails.
        ServerPlayer player = CarefulMiningContext.triggeringPlayer();
        int rank = player == null ? 0 : SkillRanks.rank(player, "hazard_management");
        double chance = Mth.clamp(bestType.baseChance() + PowersConfig.HAZARD_CHANCE_PER_RANK.get() * rank, 0.0D, 1.0D);
        return level.random.nextDouble() < chance;
    }

    public static int requiredRank(BlockState state) {
        ShoreType shore = ShoreType.from(state);
        return shore == null ? -1 : shore.requiredRank();
    }

    private record ShoreType(ResourceLocation blockId, double baseChance, int requiredRank) {
        static ShoreType from(BlockState state) {
            ResourceLocation stateId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
            for (String entry : PowersConfig.HAZARD_SHORES.get()) {
                Optional<ShoreType> parsed = parse(entry);
                if (parsed.isPresent() && parsed.get().blockId.equals(stateId)) return parsed.get();
            }
            return null;
        }

        private static Optional<ShoreType> parse(String entry) {
            try {
                String[] halves = entry.split("=", 2);
                String[] values = halves[1].split(",", 2);
                ResourceLocation blockId = ResourceLocation.parse(halves[0].trim());
                double chance = Mth.clamp(Double.parseDouble(values[0].trim()), 0.0D, 1.0D);
                int rank = Mth.clamp(Integer.parseInt(values[1].trim()), 0, 5);
                return Optional.of(new ShoreType(blockId, chance, rank));
            } catch (RuntimeException ignored) {
                return Optional.empty();
            }
        }
    }

}
