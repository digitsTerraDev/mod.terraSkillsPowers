package com.terraskills.powers.client;

import com.terraskills.powers.TerraSkillsPowers;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import java.util.Locale;

/** Renders the live handcart capacity supplied by the logical server. */
@EventBusSubscriber(value = Dist.CLIENT, modid = TerraSkillsPowers.MOD_ID)
public final class HandcartHudClient {
    private HandcartHudClient() {}

    @SubscribeEvent
    public static void renderHud(RenderGuiEvent.Post event) {
        ClientHandcartWeight.Snapshot handcart = ClientHandcartWeight.current();
        if (handcart == null) return;

        boolean pinned = handcart.cartWeightKg() > handcart.hardLimitKg();
        boolean overCapacity = handcart.cartWeightKg() > handcart.normalLimitKg();
        boolean overHalfCapacity = handcart.normalLimitKg() > 0.0D
                && handcart.cartWeightKg() > handcart.normalLimitKg() * 0.5D;
        Component text = Component.translatable(
                pinned ? "hud.terraskillspowers.handcart_too_heavy" : "hud.terraskillspowers.handcart_weight",
                formatKg(handcart.cartWeightKg()), formatKg(pinned ? handcart.hardLimitKg() : handcart.normalLimitKg()));
        int color = pinned || overCapacity ? 0xFFFF5555 : overHalfCapacity ? 0xFFFFFF00 : 0xFFFFFFFF;
        int x = (event.getGuiGraphics().guiWidth() - Minecraft.getInstance().font.width(text)) / 2;
        event.getGuiGraphics().drawString(Minecraft.getInstance().font, text, x, 8, color, true);
    }

    private static String formatKg(double weightKg) {
        return String.format(Locale.ROOT, "%.1f", weightKg);
    }
}
