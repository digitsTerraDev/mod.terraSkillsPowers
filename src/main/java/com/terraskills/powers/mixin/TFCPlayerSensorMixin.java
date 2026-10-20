package com.terraskills.powers.mixin;

import com.terraskills.powers.exploration.QuietPassage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Limits only new TFC predator player acquisition; it never clears retaliation or an existing attack target. */
@Mixin(targets = "net.minecraft.world.entity.ai.sensing.PlayerSensor")
abstract class TFCPlayerSensorMixin {
    @Redirect(method = "lambda$doTick$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;closerThan(Lnet/minecraft/world/entity/Entity;D)Z"))
    private static boolean terraskillsPowers$quietPredatorDetection(LivingEntity predator, Entity target, double range) {
        if (target instanceof ServerPlayer player && !QuietPassage.reducedPredatorRange(predator, player, range)) return false;
        return predator.closerThan(target, range);
    }
}
