package com.terraskills.powers.mixin;

import com.terraskills.powers.horse.HorseLoadState;
import com.terraskills.powers.config.PowersConfig;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import com.jusipat.astikorcartsredux.entity.AbstractDrawnEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfcastikorcarts.common.entities.carts.TFCAbstractDrawnEntity;

/** Keeps a heavily loaded cart from stepping a full block independently of its horse. */
@Mixin(AbstractDrawnEntity.class)
abstract class CartStepAssistMixin {
    @Inject(method = "maxUpStep", at = @At("RETURN"), cancellable = true)
    private void terraskillsPowers$matchLoadedHorseStepHeight(CallbackInfoReturnable<Float> callback) {
        if ((Object) this instanceof TFCAbstractDrawnEntity cart
                && cart.getPulling() instanceof AbstractHorse horse
                && HorseLoadState.hasNoStepAssist(horse.getUUID())) {
            callback.setReturnValue(PowersConfig.REDUCED_STEP_HEIGHT.get().floatValue());
        }
    }
}
