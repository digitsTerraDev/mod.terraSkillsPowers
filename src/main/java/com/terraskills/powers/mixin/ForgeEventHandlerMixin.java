package com.terraskills.powers.mixin;

import com.terraskills.powers.collapse.CarefulMiningContext;
import net.dries007.tfc.common.recipes.CollapseRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.dries007.tfc.ForgeEventHandler")
abstract class ForgeEventHandlerMixin {
    @Redirect(method = "onBlockBroken(Lnet/neoforged/neoforge/event/level/BlockEvent$BreakEvent;)V",
            at = @At(value = "INVOKE", target = "Lnet/dries007/tfc/common/recipes/CollapseRecipe;tryTriggerCollapse(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z"))
    private static boolean terraskillsPowers$carefulMining(Level level, BlockPos pos, BlockEvent.BreakEvent event) {
        return event.getPlayer() instanceof ServerPlayer player
                ? CarefulMiningContext.tryTriggerCollapse(level, pos, player)
                : CollapseRecipe.tryTriggerCollapse(level, pos);
    }
}
