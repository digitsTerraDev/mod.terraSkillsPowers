package com.terraskills.powers.mixin;

import com.terraskills.powers.weight.WeightSkillPowers;
import com.warfactory.ultimateweight.api.WeightPlayerView;
import com.warfactory.ultimateweight.core.InventoryConstraintEvaluator;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/** Applies Carry Capacity skill bonuses to Ultimate Weight's authoritative capacity calculation. */
@Mixin(InventoryConstraintEvaluator.class)
abstract class UltimateWeightCapacityMixin {
    @Inject(method = "resolveCarryCapacityKg", at = @At("RETURN"), cancellable = true)
    private void terraskillsPowers$applyCarrySkill(WeightPlayerView playerView, CallbackInfoReturnable<Double> callback) {
        try {
            UUID playerId = UUID.fromString(playerView.playerId());
            if (ServerLifecycleHooks.getCurrentServer() == null) return;
            ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(playerId);
            if (player != null) callback.setReturnValue(WeightSkillPowers.carryCapacityKg(player, callback.getReturnValue()));
        } catch (IllegalArgumentException ignored) {
            // Non-Minecraft player views have no UUID-backed server player.
        }
    }
}
