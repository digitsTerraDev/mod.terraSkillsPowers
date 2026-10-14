package com.terraskills.powers.network;

import com.terraskills.powers.client.ClientSkillRanks;
import com.terraskills.powers.client.ClientHandcartWeight;
import com.terraskills.powers.mining.BrutishMining;
import com.terraskills.powers.event.PowersEvents;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class PowersNetwork {
    private PowersNetwork() {}

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(SkillRanksPayload.TYPE, SkillRanksPayload.STREAM_CODEC,
                (payload, context) -> ClientSkillRanks.accept(payload));
        event.registrar("1").playToClient(HandcartWeightPayload.TYPE, HandcartWeightPayload.STREAM_CODEC,
                (payload, context) -> ClientHandcartWeight.accept(payload));
        event.registrar("1").playToServer(ToggleBrutishPayload.TYPE, ToggleBrutishPayload.STREAM_CODEC,
                (payload, context) -> {
                    if (context.player() instanceof ServerPlayer player) {
                        BrutishMining.toggle(player);
                        PowersEvents.syncRanks(player);
                    }
                });
    }
}
