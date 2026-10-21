package com.terraskills.powers.mixin;

import com.terraskills.powers.config.PowersConfig;
import com.terraskills.powers.exploration.TravelThirstLedger;
import com.terraskills.powers.skill.SkillRanks;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Hooks the one positive on-foot exhaustion call before TFC receives it. */
@Mixin(ServerPlayer.class)
abstract class MovementExhaustionMixin {
    @Redirect(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V"))
    private void terraskillsPowers$sourceAwareMovementExhaustion(ServerPlayer target, float exhaustion) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        boolean qualifyingSprint = exhaustion > 0.0F && player.isSprinting() && player.onGround()
                && !player.isSpectator() && !player.isPassenger() && !player.isFallFlying()
                && !player.getAbilities().flying && !player.isSwimming() && !player.isInWater();
        double enduranceReduction = Math.clamp(PowersConfig.ENDURANCE_SPRINT_EXHAUSTION_REDUCTION_PER_RANK.get()
                * Math.clamp(SkillRanks.endurance(player), 0, 5), 0.0D, 1.0D);
        float adjusted = qualifyingSprint
                ? (float) (exhaustion * (1.0D - enduranceReduction))
                : exhaustion;
        if (qualifyingSprint && adjusted > 0.0F) TravelThirstLedger.beginMovement(player);
        try {
            target.causeFoodExhaustion(adjusted);
        } finally {
            if (qualifyingSprint && adjusted > 0.0F) TravelThirstLedger.endMovement();
        }
        // Measured Pace remains intentionally inactive: TFC 4.2.10 assigns ordinary walking a zero exhaustion cost.
    }
}
