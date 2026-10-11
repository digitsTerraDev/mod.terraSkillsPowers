package com.terraskills.powers.collapse;

import com.terraskills.powers.skill.SkillRanks;
import net.dries007.tfc.common.recipes.CollapseRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

/** Limits Careful Mining to the one TFC collapse roll caused by the mining player. */
public final class CarefulMiningContext {
    private static final ThreadLocal<Double> REAL_COLLAPSE_MULTIPLIER = ThreadLocal.withInitial(() -> 1.0D);

    private CarefulMiningContext() {}

    public static boolean tryTriggerCollapse(Level level, BlockPos pos, ServerPlayer player) {
        int rank = SkillRanks.rank(player, "careful_mining");
        if (rank == 0) return CollapseRecipe.tryTriggerCollapse(level, pos);

        REAL_COLLAPSE_MULTIPLIER.set(1.0D - 0.10D * rank);
        try {
            return CollapseRecipe.tryTriggerCollapse(level, pos);
        } finally {
            REAL_COLLAPSE_MULTIPLIER.remove();
        }
    }

    /** Used only by the ordinal-zero random roll in TFC's tryTriggerCollapse method. */
    public static float adjustRealCollapseRoll(float roll) {
        return (float) (roll / REAL_COLLAPSE_MULTIPLIER.get());
    }
}
