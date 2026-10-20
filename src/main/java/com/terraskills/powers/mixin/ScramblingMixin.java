package com.terraskills.powers.mixin;

import com.terraskills.powers.exploration.ScramblingPowers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Multiplies only the positive result of vanilla's actual ladder/vine movement clamp. */
@Mixin(LivingEntity.class)
abstract class ScramblingMixin {
    @Inject(method = "handleOnClimbable", at = @At("RETURN"), cancellable = true)
    private void terraskillsPowers$scramble(Vec3 requested, CallbackInfoReturnable<Vec3> callback) {
        if (!((Object) this instanceof Player player) || !((LivingEntity) (Object) this).onClimbable()) return;
        Vec3 vanilla = callback.getReturnValue();
        if (requested.y <= 0.0D || vanilla.y <= 0.0D) return;
        double multiplier = ScramblingPowers.multiplier(player);
        if (multiplier > 1.0D) callback.setReturnValue(new Vec3(vanilla.x, vanilla.y * multiplier, vanilla.z));
    }
}
