package com.terraskills.powers.mixin;

import com.terraskills.powers.exploration.QuietPassage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Adjusts TFC's prey sensor, retaining its existing scary-mob and memory logic. */
@Pseudo
@Mixin(targets = "net.dries007.tfc.common.entities.ai.prey.ScareSensor")
abstract class TFCScareSensorMixin {
    @Redirect(method = "lambda$checkForMobsNearby$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;closerThan(Lnet/minecraft/world/entity/Entity;DD)Z"))
    private boolean terraskillsPowers$quietPreyDetection(LivingEntity candidate, Entity prey, double horizontal, double vertical) {
        if (candidate instanceof ServerPlayer player && prey instanceof LivingEntity living
                && !QuietPassage.reducedPreyRange(living, player, horizontal, vertical)) return false;
        return candidate.closerThan(prey, horizontal, vertical);
    }
}
