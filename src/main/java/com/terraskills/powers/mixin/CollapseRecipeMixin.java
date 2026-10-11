package com.terraskills.powers.mixin;

import com.terraskills.powers.collapse.CarefulMiningContext;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.dries007.tfc.common.recipes.CollapseRecipe")
abstract class CollapseRecipeMixin {
    @Redirect(method = "tryTriggerCollapse(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextFloat()F", ordinal = 0))
    private static float terraskillsPowers$reduceRealCollapseChance(RandomSource random) {
        return CarefulMiningContext.adjustRealCollapseRoll(random.nextFloat());
    }
}
