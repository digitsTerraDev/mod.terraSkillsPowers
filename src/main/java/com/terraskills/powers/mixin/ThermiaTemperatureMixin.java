package com.terraskills.powers.mixin;

import com.nyonyix.thermia.data.attachment.EntityTemperature;
import com.terraskills.powers.exploration.AcclimatizationPowers;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Hooks Thermia's native post-clothing/shelter temperature calculation, not climate or damage globally. */
@Pseudo
@Mixin(targets = "com.nyonyix.thermia.data.manager.EntityTemperatureManager")
abstract class ThermiaTemperatureMixin {
    private static final ThreadLocal<Context> TERRASKILLS_POWERS_CONTEXT = new ThreadLocal<>();

    @Inject(method = "handleTemperatureChange", at = @At("HEAD"))
    private static void terraskillsPowers$captureTemperatureContext(Entity entity, EntityTemperature data,
                                                                    CallbackInfoReturnable<EntityTemperature> callback) {
        TERRASKILLS_POWERS_CONTEXT.set(new Context(entity, data));
    }

    @ModifyArg(method = "handleTemperatureChange", at = @At(value = "INVOKE",
            target = "Lcom/nyonyix/thermia/data/attachment/EntityTemperature;withInternalTemperature(F)Lcom/nyonyix/thermia/data/attachment/EntityTemperature;"), index = 0)
    private static float terraskillsPowers$reduceHarmfulExposure(float proposed) {
        Context context = TERRASKILLS_POWERS_CONTEXT.get();
        return context == null ? proposed : AcclimatizationPowers.reduceHarmfulExposure(context.entity(), context.data(), proposed);
    }

    @Inject(method = "handleTemperatureChange", at = @At("RETURN"))
    private static void terraskillsPowers$clearTemperatureContext(Entity entity, EntityTemperature data,
                                                                  CallbackInfoReturnable<EntityTemperature> callback) {
        TERRASKILLS_POWERS_CONTEXT.remove();
    }

    private record Context(Entity entity, EntityTemperature data) {}
}
