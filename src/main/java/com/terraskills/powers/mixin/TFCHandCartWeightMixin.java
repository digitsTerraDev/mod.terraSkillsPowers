package com.terraskills.powers.mixin;

import com.warfactory.ultimateweight.UltimateWeightCommon;
import com.warfactory.ultimateweight.v1211.WeightViews1211;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import com.jusipat.astikorcartsredux.entity.AbstractDrawnEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfcastikorcarts.config.TFCAstikorCartsConfig;
import net.dries007.tfc.common.effect.TFCEffects;
import tfcastikorcarts.common.entities.carts.TFCAbstractDrawnInventoryEntity;
import tfcastikorcarts.common.entities.carts.TFCHandCartEntity;

import java.util.function.Supplier;

/** Replaces TFC Astikor's fixed handcart thresholds with Ultimate Weight capacity-based thresholds. */
@Mixin(TFCAbstractDrawnInventoryEntity.class)
abstract class TFCHandCartWeightMixin {
    private static final double CART_CAPACITY_MULTIPLIER = 5.0D;
    private static final double OVERLOAD_ALLOWANCE = 1.20D;
    // Ultimate Weight's 120 kg absolute maximum begins to encumber at 100 kg.
    private static final double SOFT_CARRY_CAPACITY_MULTIPLIER = 5.0D / 6.0D;

    @Shadow public abstract int getContainerSize();
    @Shadow public abstract ItemStack getItem(int slot);
    @Shadow public abstract double countOverburdenedCargo();

    @Inject(method = "countOverburdenedCargo", at = @At("HEAD"), cancellable = true)
    private void terraskillsPowers$useUltimateWeightForHandcart(CallbackInfoReturnable<Double> cir) {
        if (terraskillsPowers$isHandcart()) {
            cir.setReturnValue(terraskillsPowers$cargoWeightKg());
        }
    }

    /**
     * Astikor 1.2.4 tries to apply TFC's removed PINNED effect at its hard threshold.
     * Stop a too-heavy handcart before Astikor executes that incompatible branch.
     */
    @Inject(method = "pulledTick", at = @At("HEAD"), cancellable = true)
    private void terraskillsPowers$blockTooHeavyHandcart(CallbackInfo ci) {
        if (!(terraskillsPowers$getPulling() instanceof ServerPlayer player)) return;

        boolean tooHeavy = terraskillsPowers$isHandcart()
                ? terraskillsPowers$cargoWeightKg() > terraskillsPowers$handcartLimitKg() * OVERLOAD_ALLOWANCE
                : countOverburdenedCargo() * 0.01D > (Double) TFCAstikorCartsConfig.COMMON.pinnedThreshold.get();
        if (tooHeavy) {
            player.displayClientMessage(Component.translatable("message.terraskillspowers.handcart_too_heavy"), true);
            ci.cancel();
        }
    }

    @Redirect(method = "pulledTick", at = @At(value = "INVOKE", target = "Ljava/util/function/Supplier;get()Ljava/lang/Object;", ordinal = 0))
    private Object terraskillsPowers$dynamicPinnedThreshold(Supplier<?> original) {
        return terraskillsPowers$capacityLimit(OVERLOAD_ALLOWANCE, original);
    }

    @Redirect(method = "pulledTick", at = @At(value = "INVOKE", target = "Ljava/util/function/Supplier;get()Ljava/lang/Object;", ordinal = 1))
    private Object terraskillsPowers$dynamicOverburdenedThreshold(Supplier<?> original) {
        return terraskillsPowers$capacityLimit(1.0D, original);
    }

    @Redirect(method = "pulledTick", at = @At(value = "INVOKE", target = "Ljava/util/function/Supplier;get()Ljava/lang/Object;", ordinal = 2))
    private Object terraskillsPowers$disableExhaustedThreshold(Supplier<?> original) {
        return terraskillsPowers$isHandcart() ? Double.MAX_VALUE : original.get();
    }

    /** TFC Astikor 1.2.4 references the removed TFCEffects.PINNED field. */
    @Redirect(method = "pulledTick", at = @At(value = "FIELD", target = "Lnet/dries007/tfc/common/effect/TFCEffects;PINNED:Lnet/dries007/tfc/common/effect/TFCEffects$Id;"))
    private TFCEffects.Id<MobEffect> terraskillsPowers$replaceRemovedPinnedEffect() {
        if (terraskillsPowers$getPulling() instanceof ServerPlayer player) {
            player.displayClientMessage(Component.translatable("message.terraskillspowers.handcart_too_heavy"), true);
        }
        return TFCEffects.OVERBURDENED;
    }

    private Object terraskillsPowers$capacityLimit(double multiplier, Supplier<?> original) {
        return terraskillsPowers$isHandcart() && terraskillsPowers$getPulling() instanceof ServerPlayer
                // TFC compares cargo after converting it from kg to 100-kg units.
                ? terraskillsPowers$handcartLimitKg() * multiplier * 0.01D
                : original.get();
    }

    private boolean terraskillsPowers$isHandcart() {
        return (Object) this instanceof TFCHandCartEntity;
    }

    private double terraskillsPowers$handcartLimitKg() {
        if (!(terraskillsPowers$getPulling() instanceof ServerPlayer player)) return 0.0D;
        double capacity = UltimateWeightCommon.bootstrap().constraintEvaluator()
                .resolveCarryCapacityKg(WeightViews1211.player(player));
        double normalCarryCapacity = capacity * SOFT_CARRY_CAPACITY_MULTIPLIER;
        double remainingCapacity = Math.max(0.0D, normalCarryCapacity - WeightViews1211.totalWeight(player));
        return remainingCapacity * CART_CAPACITY_MULTIPLIER;
    }

    private double terraskillsPowers$cargoWeightKg() {
        double total = 0.0D;
        for (int slot = 0; slot < getContainerSize(); slot++) {
            total += WeightViews1211.weightOf(getItem(slot));
        }
        return total;
    }

    private Entity terraskillsPowers$getPulling() {
        return ((AbstractDrawnEntity) (Object) this).getPulling();
    }
}
