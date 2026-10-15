package com.terraskills.powers.mixin;

import com.terraskills.powers.horse.HorseLoadState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import tfcastikorcarts.common.entities.carts.TFCAbstractDrawnEntity;

/** A hitched horse stays parked without suppressing its normal AI and idle behavior. */
@Mixin(LivingEntity.class)
abstract class HorseCartImmobilityMixin {
    @ModifyVariable(method = "travel", at = @At("HEAD"), argsOnly = true)
    private Vec3 terraskillsPowers$parkUnriddenHitchedHorse(Vec3 movement) {
        if ((Object) this instanceof AbstractHorse horse && HorseLoadState.isMovementBlocked(horse.getUUID())) {
            horse.setDeltaMovement(Vec3.ZERO);
            return Vec3.ZERO;
        }
        if (terraskillsPowers$isUnriddenHitchedHorse()) {
            ((AbstractHorse) (Object) this).setDeltaMovement(Vec3.ZERO);
            return Vec3.ZERO;
        }
        if ((Object) this instanceof AbstractHorse horse) {
            return movement.scale(HorseLoadState.speedMultiplier(horse.getUUID()));
        }
        return movement;
    }

    private boolean terraskillsPowers$isUnriddenHitchedHorse() {
        if (!((Object) this instanceof AbstractHorse horse) || horse.getControllingPassenger() != null) return false;
        return horse.level().getEntitiesOfClass(TFCAbstractDrawnEntity.class,
                horse.getBoundingBox().inflate(12.0D), cart -> cart.getPulling() == horse).size() > 0;
    }
}
