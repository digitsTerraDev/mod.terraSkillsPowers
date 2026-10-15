package com.terraskills.powers.mixin;

import com.terraskills.powers.horse.HorseLoadState;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Stops the rider's actual horse-jump input once its carried load reaches the limit. */
@Mixin(AbstractHorse.class)
abstract class HorseJumpMixin {
    @Inject(method = "handleStartJump", at = @At("HEAD"), cancellable = true)
    private void terraskillsPowers$blockOverloadedJump(int jumpPower, CallbackInfo callback) {
        if (HorseLoadState.isJumpBlocked(((AbstractHorse) (Object) this).getUUID())) callback.cancel();
    }

    @Inject(method = "executeRidersJump", at = @At("HEAD"), cancellable = true)
    private void terraskillsPowers$blockOverloadedJumpVelocity(float jumpScale, Vec3 movement, CallbackInfo callback) {
        if (HorseLoadState.isJumpBlocked(((AbstractHorse) (Object) this).getUUID())) callback.cancel();
    }
}
