package com.terraskills.powers.mixin;

import com.terraskills.powers.collapse.CarefulMiningContext;
import com.terraskills.powers.hazard.HazardManagement;
import net.dries007.tfc.common.recipes.CollapseRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.dries007.tfc.common.recipes.CollapseRecipe")
abstract class CollapseRecipeMixin {
    @Redirect(method = "tryTriggerCollapse(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextFloat()F", ordinal = 0))
    private static float terraskillsPowers$reduceRealCollapseChance(RandomSource random) {
        return CarefulMiningContext.adjustRealCollapseRoll(random.nextFloat());
    }

    @Inject(method = "startCollapse(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z", at = @At("HEAD"))
    private static void terraskillsPowers$beginHazardFront(Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        HazardManagement.beginCollapse();
    }

    @Inject(method = "startCollapse(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z", at = @At("RETURN"))
    private static void terraskillsPowers$endHazardFront(Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        HazardManagement.endCollapse();
    }

    @Redirect(method = "startCollapse(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z",
            at = @At(value = "INVOKE", target = "Lnet/dries007/tfc/common/recipes/CollapseRecipe;collapseBlock(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)Z"))
    private static boolean terraskillsPowers$arrestPropagationFront(Level level, BlockPos pos, BlockState state, boolean propagate) {
        return propagate && HazardManagement.tryArrest(level, pos)
                ? false
                : CollapseRecipe.collapseBlock(level, pos, state, propagate);
    }
}
