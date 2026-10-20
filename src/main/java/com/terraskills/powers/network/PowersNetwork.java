package com.terraskills.powers.network;

import com.terraskills.powers.client.ClientSkillRanks;
import com.terraskills.powers.client.ClientHandcartWeight;
import com.terraskills.powers.client.ClientHorseWeight;
import com.terraskills.powers.client.ClientActivityHeatmap;
import com.terraskills.powers.exploration.ActivityHeatmapService;
import com.terraskills.powers.mining.BrutishMining;
import com.terraskills.powers.event.PowersEvents;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class PowersNetwork {
    private PowersNetwork() {}

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("5").playToClient(SkillRanksPayload.TYPE, SkillRanksPayload.STREAM_CODEC,
                (payload, context) -> ClientSkillRanks.accept(payload));
        event.registrar("5").playToClient(HandcartWeightPayload.TYPE, HandcartWeightPayload.STREAM_CODEC,
                (payload, context) -> ClientHandcartWeight.accept(payload));
        event.registrar("5").playToClient(HorseWeightPayload.TYPE, HorseWeightPayload.STREAM_CODEC,
                (payload, context) -> ClientHorseWeight.accept(payload));
        event.registrar("5").playToClient(ActivityHeatmapPayload.TYPE, ActivityHeatmapPayload.STREAM_CODEC,
                (payload, context) -> ClientActivityHeatmap.accept(payload));
        event.registrar("5").playToClient(ActivityHeatmapControlPayload.TYPE, ActivityHeatmapControlPayload.STREAM_CODEC,
                (payload, context) -> ClientActivityHeatmap.acceptControl(payload));
        event.registrar("5").playToServer(ToggleBrutishPayload.TYPE, ToggleBrutishPayload.STREAM_CODEC,
                (payload, context) -> {
                    if (context.player() instanceof ServerPlayer player) {
                        BrutishMining.toggle(player);
                        PowersEvents.syncRanks(player);
                    }
                });
        event.registrar("5").playToServer(ActivityHeatmapRequestPayload.TYPE, ActivityHeatmapRequestPayload.STREAM_CODEC,
                (payload, context) -> {
                    if (context.player() instanceof ServerPlayer player) {
                        ActivityHeatmapService.handleRequest(player, payload);
                    }
                });
    }
}
