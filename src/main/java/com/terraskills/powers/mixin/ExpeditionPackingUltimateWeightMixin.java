package com.terraskills.powers.mixin;

import com.terraskills.powers.weight.ExpeditionPackingWeights;
import com.warfactory.ultimateweight.api.WeightStackView;
import com.warfactory.ultimateweight.v1211.WeightViews1211;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

/** Keeps pickup, transfer, inventory-delta, and native tooltip calculations in the same per-player context. */
@Mixin(targets = "com.warfactory.ultimateweight.v1211.UltimateWeight1211")
abstract class ExpeditionPackingUltimateWeightMixin {
    @Redirect(method = "pickupBlockMessage", at = @At(value = "INVOKE", target = "Lcom/warfactory/ultimateweight/v1211/WeightViews1211;stack(Lnet/minecraft/world/item/ItemStack;)Lcom/warfactory/ultimateweight/api/WeightStackView;"))
    private static WeightStackView terraskillsPowers$pickupStackContext(ItemStack stack, ServerPlayer player, ItemStack picked) {
        return ExpeditionPackingWeights.wrap(player, WeightViews1211.stack(stack));
    }

    @Redirect(method = "pickupBlockMessage", at = @At(value = "INVOKE", target = "Lcom/warfactory/ultimateweight/v1211/WeightViews1211;weightOf(Lnet/minecraft/world/item/ItemStack;)D"))
    private static double terraskillsPowers$effectivePickupWeight(ItemStack stack, ServerPlayer player, ItemStack picked) {
        return ExpeditionPackingWeights.effectiveStackWeightKg(player, stack);
    }

    @Redirect(method = "isTransferAllowed", at = @At(value = "INVOKE", target = "Lcom/warfactory/ultimateweight/v1211/WeightViews1211;stack(Lnet/minecraft/world/item/ItemStack;)Lcom/warfactory/ultimateweight/api/WeightStackView;"))
    private static WeightStackView terraskillsPowers$transferStackContext(ItemStack stack, Player player, Slot slot, ClickType clickType, int button) {
        return ExpeditionPackingWeights.wrap(player, WeightViews1211.stack(stack));
    }

    @Redirect(method = "onClientInventoryDelta", at = @At(value = "INVOKE", target = "Lcom/warfactory/ultimateweight/v1211/WeightViews1211;weightOf(Lnet/minecraft/world/item/ItemStack;)D"))
    private static double terraskillsPowers$effectiveClientDelta(ItemStack stack, Player player, int slot, ItemStack before, ItemStack after) {
        return ExpeditionPackingWeights.effectiveStackWeightKg(player, stack);
    }

    @Redirect(method = "onServerInventoryDelta", at = @At(value = "INVOKE", target = "Lcom/warfactory/ultimateweight/v1211/WeightViews1211;weightOf(Lnet/minecraft/world/item/ItemStack;)D"))
    private static double terraskillsPowers$effectiveServerDelta(ItemStack stack, ServerPlayer player, int slot, ItemStack before, ItemStack after) {
        return ExpeditionPackingWeights.effectiveStackWeightKg(player, stack);
    }

    @Redirect(method = "appendTooltip(Lnet/minecraft/world/item/ItemStack;Ljava/util/List;Lnet/minecraft/world/entity/player/Player;)V", at = @At(value = "INVOKE", target = "Lcom/warfactory/ultimateweight/v1211/WeightViews1211;stack(Lnet/minecraft/world/item/ItemStack;)Lcom/warfactory/ultimateweight/api/WeightStackView;"))
    private static WeightStackView terraskillsPowers$tooltipStackContext(ItemStack stack, ItemStack tooltipStack, List<Component> tooltip, Player player) {
        return ExpeditionPackingWeights.wrap(player, WeightViews1211.stack(stack));
    }
}
