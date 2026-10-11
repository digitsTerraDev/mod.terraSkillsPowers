package com.terraskills.powers.network;

import com.terraskills.powers.client.ClientSkillRanks;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class PowersNetwork {
    private PowersNetwork() {}

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(SkillRanksPayload.TYPE, SkillRanksPayload.STREAM_CODEC,
                (payload, context) -> ClientSkillRanks.accept(payload));
    }
}
