package com.terraskills.powers.client;

import com.terraskills.powers.TerraSkillsPowers;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/** Clears client-only caches after leaving a world. */
@EventBusSubscriber(value = Dist.CLIENT, modid = TerraSkillsPowers.MOD_ID)
public final class ClientStateCleanup {
    private ClientStateCleanup() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().player != null) return;
        ClientSkillRanks.clear();
        ClientHandcartWeight.clear();
        ClientHorseWeight.clear();
        ClientActivityHeatmap.clear();
    }
}
