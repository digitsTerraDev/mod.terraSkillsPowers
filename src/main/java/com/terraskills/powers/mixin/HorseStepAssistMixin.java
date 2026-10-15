package com.terraskills.powers.mixin;

import com.terraskills.powers.horse.HorseLoadState;
import com.terraskills.powers.config.PowersConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Removes a loaded horse's extra step height while preserving its normal behavior otherwise. */
@Mixin(LivingEntity.class)
abstract class HorseStepAssistMixin {
    @Inject(method = "maxUpStep", at = @At("RETURN"), cancellable = true)
    private void terraskillsPowers$removeStepAssist(CallbackInfoReturnable<Float> callback) {
        if ((Object) this instanceof AbstractHorse horse && HorseLoadState.hasNoStepAssist(horse.getUUID())) {
            // Vanilla-sized stepping still handles slabs, but no longer clears a full block.
            callback.setReturnValue(PowersConfig.REDUCED_STEP_HEIGHT.get().floatValue());
        }
    }
}
