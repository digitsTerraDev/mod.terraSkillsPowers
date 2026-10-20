package com.terraskills.powers.mixin;

import com.terraskills.powers.exploration.TravelThirstLedger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** TFC 4.2.10 source-aware thirst conversion; no periodic restoration or global thirst multiplier is used. */
@Pseudo
@Mixin(targets = "net.dries007.tfc.common.player.PlayerInfo")
abstract class TFCPlayerInfoTravelThirstMixin {
    @Shadow @Final private Player player;
    @Shadow public abstract float getThirstContributionFromTemperature();

    @Inject(method = "addExhaustion", at = @At("HEAD"))
    private void terraskillsPowers$recordExhaustion(float amount, CallbackInfo callback) {
        TravelThirstLedger.record(player, amount);
    }

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/dries007/tfc/common/player/PlayerInfo;addThirst(F)V", ordinal = 1))
    private float terraskillsPowers$discountTravelOnly(float loss) {
        return player instanceof ServerPlayer server
                ? TravelThirstLedger.discountTravelThirst(server, loss, getThirstContributionFromTemperature()) : loss;
    }
}
