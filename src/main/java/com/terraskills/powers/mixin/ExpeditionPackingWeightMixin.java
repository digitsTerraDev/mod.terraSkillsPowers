package com.terraskills.powers.mixin;

import com.terraskills.powers.weight.ExpeditionPackingWeights;
import com.terraskills.powers.weight.PlayerOwnedWeightStackView;
import com.warfactory.ultimateweight.api.WeightStackView;
import com.warfactory.ultimateweight.core.ResolvedWeight;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Discounts only tagged top-level shells after Ultimate Weight has resolved their contents. */
@Mixin(targets = "com.warfactory.ultimateweight.core.WeightResolver")
abstract class ExpeditionPackingWeightMixin {
    @Inject(method = "resolve", at = @At("RETURN"), cancellable = true)
    private void terraskillsPowers$resolveEffectivePlayerWeight(WeightStackView view, CallbackInfoReturnable<ResolvedWeight> callback) {
        if (!(view instanceof PlayerOwnedWeightStackView owned) || !(owned.unwrap() instanceof ItemStack stack)) return;
        ResolvedWeight resolved = callback.getReturnValue();
        double effective = ExpeditionPackingWeights.effectiveSingleWeightKg(owned.player(), stack, owned.resolutionDepth(), resolved.singleItemWeightKg());
        if (effective != resolved.singleItemWeightKg()) {
            callback.setReturnValue(new ResolvedWeight(effective, owned.count(), resolved.source(), resolved.matchedKey()));
        }
    }
}
