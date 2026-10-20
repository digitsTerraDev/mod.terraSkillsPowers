package com.terraskills.powers.exploration;

import com.terraskills.powers.skill.SkillRanks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/** Narrow AI-sensor predicates used by the TFC prey and predator hooks. */
public final class QuietPassage {
    private static final Class<?> TFC_PREDATOR = findPredatorClass();

    private QuietPassage() {}

    public static boolean reducedPreyRange(LivingEntity prey, ServerPlayer player, double horizontal, double vertical) {
        if (!applies(player)) return true;
        double scale = scale(player);
        double dx = prey.getX() - player.getX();
        double dz = prey.getZ() - player.getZ();
        return dx * dx + dz * dz <= horizontal * horizontal * scale * scale
                && Math.abs(prey.getY() - player.getY()) <= vertical * scale;
    }

    public static boolean reducedPredatorRange(LivingEntity predator, ServerPlayer player, double vanillaRange) {
        if (!isTfcPredator(predator) || !applies(player)) return true;
        double range = vanillaRange * scale(player);
        return predator.distanceToSqr(player) <= range * range;
    }

    private static boolean applies(ServerPlayer player) {
        return !player.isSpectator() && player.isShiftKeyDown() && !player.isPassenger() && !player.isFallFlying()
                && !player.getAbilities().flying && !player.isSwimming() && !player.isInWater()
                && SkillRanks.quietPassage(player) > 0;
    }

    private static double scale(ServerPlayer player) {
        return 1.0D - 0.05D * Math.clamp(SkillRanks.quietPassage(player), 0, 5);
    }

    private static boolean isTfcPredator(Entity entity) {
        return TFC_PREDATOR != null && TFC_PREDATOR.isInstance(entity);
    }

    private static Class<?> findPredatorClass() {
        try {
            return Class.forName("net.dries007.tfc.common.entities.predator.Predator", false, QuietPassage.class.getClassLoader());
        } catch (ClassNotFoundException ignored) {
            return null;
        }
    }
}
