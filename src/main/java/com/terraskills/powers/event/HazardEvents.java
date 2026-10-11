package com.terraskills.powers.event;

import com.terraskills.powers.TerraSkillsPowers;
import com.terraskills.powers.hazard.HazardManagement;
import com.terraskills.powers.skill.SkillRanks;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = TerraSkillsPowers.MOD_ID)
public final class HazardEvents {
    private HazardEvents() {}

    @SubscribeEvent
    public static void gateShorePlacement(BlockEvent.EntityPlaceEvent event) {
        int requiredRank = HazardManagement.requiredRank(event.getPlacedBlock());
        if (requiredRank < 0) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            if (!com.terraskills.powers.config.PowersConfig.HAZARD_ALLOW_AUTOMATED_PLACEMENT.get()) event.setCanceled(true);
            return;
        }
        if (SkillRanks.rank(player, "hazard_management") < requiredRank) {
            event.setCanceled(true);
            player.displayClientMessage(Component.translatable("message.terraskillspowers.hazard.rank_required", requiredRank), true);
        }
    }
}
