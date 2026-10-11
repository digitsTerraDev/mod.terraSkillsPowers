package com.terraskills.powers.mixin;

import com.terraskills.powers.mining.MiningDurability;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.dries007.tfc.common.items.ToolItem")
abstract class ToolItemMixin {
    @Redirect(method = "mineBlock(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/LivingEntity;)Z",
            at = @At(value = "INVOKE", target = "Lnet/dries007/tfc/util/Helpers;damageItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;)V"))
    private void terraskillsPowers$applyToolConservation(ItemStack stack, LivingEntity miner, EquipmentSlot slot) {
        MiningDurability.damageForMinedBlock(stack, miner, slot);
    }
}
