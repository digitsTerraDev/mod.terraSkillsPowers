package com.terraskills.powers.mixin;

import com.terraskills.powers.weight.ExpeditionPackingWeights;
import com.warfactory.ultimateweight.api.WeightStackView;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

/** Adds ownership context to Ultimate Weight's top-level player inventory views, on both logical sides. */
@Mixin(targets = "com.warfactory.ultimateweight.v1211.WeightViews1211$PlayerView")
abstract class ExpeditionPackingPlayerViewMixin {
    @Shadow @Final private Player player;

    @Inject(method = "inventory", at = @At("RETURN"), cancellable = true)
    private void terraskillsPowers$attachPlayerContext(CallbackInfoReturnable<Iterable<? extends WeightStackView>> callback) {
        List<WeightStackView> views = new ArrayList<>();
        for (WeightStackView view : callback.getReturnValue()) views.add(ExpeditionPackingWeights.wrap(player, view));
        callback.setReturnValue(views);
    }
}
