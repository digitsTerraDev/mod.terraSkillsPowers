package com.terraskills.powers.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.terraskills.powers.TerraSkillsPowers;
import com.terraskills.powers.network.ToggleBrutishPayload;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(value = Dist.CLIENT, modid = TerraSkillsPowers.MOD_ID)
public final class BrutishMiningClient {
    private static final KeyMapping TOGGLE = new KeyMapping("key.terraskillspowers.toggle_brutish",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, "key.categories.terraskillspowers");

    private BrutishMiningClient() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().player == null) {
            ClientSkillRanks.clear();
            return;
        }
        while (TOGGLE.consumeClick()) PacketDistributor.sendToServer(ToggleBrutishPayload.INSTANCE);
    }

    @SubscribeEvent
    public static void renderHud(RenderGuiEvent.Post event) {
        if (ClientSkillRanks.brutishActive()) {
            event.getGuiGraphics().drawString(net.minecraft.client.Minecraft.getInstance().font,
                    Component.translatable("hud.terraskillspowers.brutish_active"), 8, 8, 0xFFFFAA00, true);
        }
    }

    @EventBusSubscriber(value = Dist.CLIENT, modid = TerraSkillsPowers.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    public static final class ModEvents {
        @SubscribeEvent
        public static void registerKeyMapping(RegisterKeyMappingsEvent event) {
            event.register(TOGGLE);
        }
    }
}
