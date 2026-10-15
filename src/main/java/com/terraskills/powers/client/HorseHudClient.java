package com.terraskills.powers.client;

import com.terraskills.powers.TerraSkillsPowers;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import java.util.Locale;

/** Displays the rider and cart load against the ridden horse's jump-based capacity. */
@EventBusSubscriber(value = Dist.CLIENT, modid = TerraSkillsPowers.MOD_ID)
public final class HorseHudClient {
    private HorseHudClient() {}

    @SubscribeEvent
    public static void renderHud(RenderGuiEvent.Post event) {
        ClientHorseWeight.Snapshot horse = ClientHorseWeight.current();
        if (horse == null) return;

        int color = weightColor(horse.loadWeightKg(), horse.capacityKg());
        Component text = Component.translatable("hud.terraskillspowers.horse_weight",
                formatKg(horse.loadWeightKg()), formatKg(horse.capacityKg()));
        int x = (event.getGuiGraphics().guiWidth() - Minecraft.getInstance().font.width(text)) / 2;
        event.getGuiGraphics().drawString(Minecraft.getInstance().font, text, x, 8, color, true);

        Component backText = Component.translatable("hud.terraskillspowers.horse_back_weight",
                formatKg(horse.backWeightKg()), formatKg(horse.backCapacityKg()));
        int backColor = weightColor(horse.backWeightKg(), horse.backCapacityKg());
        int backX = (event.getGuiGraphics().guiWidth() - Minecraft.getInstance().font.width(backText)) / 2;
        event.getGuiGraphics().drawString(Minecraft.getInstance().font, backText, backX, 18, backColor, true);
    }

    private static String formatKg(double weightKg) {
        return String.format(Locale.ROOT, "%.1f", weightKg);
    }

    private static int weightColor(double weightKg, double capacityKg) {
        if (weightKg > capacityKg) return 0xFFFF5555;
        if (capacityKg > 0.0D && weightKg > capacityKg * 0.6D) return 0xFFFFAA00;
        if (capacityKg > 0.0D && weightKg > capacityKg * 0.3D) return 0xFFFFFF00;
        return 0xFFFFFFFF;
    }
}
