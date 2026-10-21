package com.terraskills.powers.exploration;

import com.terraskills.powers.config.PowersConfig;
import com.terraskills.powers.skill.SkillRanks;
import net.minecraft.world.entity.player.Player;

/** Shared eligibility guard for the climbing mixin. */
public final class ScramblingPowers {
    private ScramblingPowers() {}

    public static double multiplier(Player player) {
        if (player.isSpectator() || player.isPassenger() || player.isFallFlying() || player.getAbilities().flying
                || player.isSwimming() || player.isInWater()) return 1.0D;
        return 1.0D + PowersConfig.SCRAMBLING_UPWARD_SPEED_BONUS_PER_RANK.get()
                * Math.clamp(SkillRanks.scrambling(player), 0, 5);
    }
}
