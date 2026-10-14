package com.terraskills.powers.event;

import com.terraskills.powers.TerraSkillsPowers;
import com.terraskills.powers.mining.ExcavationReach;
import com.terraskills.powers.mining.BrutishMining;
import com.terraskills.powers.mining.MiningDurability;
import com.terraskills.powers.client.ClientSkillRanks;
import com.terraskills.powers.network.SkillRanksPayload;
import com.terraskills.powers.network.HandcartWeightPayload;
import com.terraskills.powers.skill.SkillRanks;
import com.warfactory.ultimateweight.UltimateWeightCommon;
import com.warfactory.ultimateweight.v1211.WeightViews1211;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.data.Support;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import tfcastikorcarts.common.entities.carts.TFCHandCartEntity;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = TerraSkillsPowers.MOD_ID)
public final class PowersEvents {
    private static final long ASSESSMENT_COOLDOWN_TICKS = 100L;
    private static final double HANDCART_CAPACITY_MULTIPLIER = 5.0D;
    private static final double HANDCART_OVERLOAD_ALLOWANCE = 1.20D;
    private static final double SOFT_CARRY_CAPACITY_MULTIPLIER = 5.0D / 6.0D;
    private static final Map<UUID, AssessmentCooldown> LAST_ASSESSMENT = new HashMap<>();
    private PowersEvents() {}

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        ItemStack tool = player.getMainHandItem();
        BlockState state = event.getState();
        float multiplier = 1.0F;

        if (tool.is(ItemTags.PICKAXES)) {
            multiplier += 0.05F * SkillRanks.mining(player);
            if (player.blockPosition().getY() < 0) multiplier += 0.05F * SkillRanks.deepMining(player);
            if (state.is(TFCTags.Blocks.STONES_RAW)) multiplier += 0.04F * SkillRanks.stoneMining(player);
        }
        if (tool.is(ItemTags.SHOVELS) && state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            multiplier += 0.03F * SkillRanks.excavating(player);
        }
        if (tool.is(ItemTags.PICKAXES) || tool.is(ItemTags.SHOVELS)) {
            int brutishRank = player instanceof ServerPlayer server ? BrutishMining.activeRank(server)
                    : ClientSkillRanks.brutishActive() ? ClientSkillRanks.brutishMining() : 0;
            multiplier += 0.50F * brutishRank;
        }
        if (multiplier != 1.0F) event.setNewSpeed(event.getNewSpeed() * multiplier);
    }

    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            event.setDistance(Math.max(0, event.getDistance() - 0.5F * SkillRanks.heightsTraining(player)));
        }
    }

    @SubscribeEvent
    public static void onJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        TFCHandCartEntity handcart = findPulledHandcart(player);
        if (handcart == null) return;

        double normalLimitKg = normalHandcartLimitKg(player);
        if (cartWeightKg(handcart) > normalLimitKg * 0.5D) {
            // LivingJumpEvent fires after the jump velocity is applied, so clear only its vertical component.
            player.setDeltaMovement(player.getDeltaMovement().x, 0.0D, player.getDeltaMovement().z);
            player.hurtMarked = true;
        }
    }

    @SubscribeEvent
    public static void onAssessment(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || event.getHand() != net.minecraft.world.InteractionHand.MAIN_HAND
                || !player.isShiftKeyDown() || !player.getMainHandItem().is(ItemTags.PICKAXES)) return;

        int rank = SkillRanks.rank(player, "support_assessment");
        if (rank == 0) return;

        ResourceKey<net.minecraft.world.level.Level> dimension = player.level().dimension();
        long now = player.serverLevel().getGameTime();
        AssessmentCooldown previous = LAST_ASSESSMENT.get(player.getUUID());
        if (previous != null && previous.dimension().equals(dimension) && now >= previous.gameTime()
                && now - previous.gameTime() < ASSESSMENT_COOLDOWN_TICKS) {
            player.displayClientMessage(Component.translatable("message.terraskillspowers.assessment.cooldown")
                    .withStyle(ChatFormatting.GRAY), true);
            return;
        }
        LAST_ASSESSMENT.put(player.getUUID(), new AssessmentCooldown(dimension, now));

        int radius = rank - 1;
        BlockPos center = event.getPos();
        int candidates = (int) Support.findUnsupportedPositions(player.serverLevel(),
                        center.offset(-radius, -radius, -radius), center.offset(radius, radius, radius)).stream()
                .filter(pos -> player.serverLevel().getBlockState(pos).is(TFCTags.Blocks.CAN_START_COLLAPSE)).count();
        player.displayClientMessage(Component.translatable("message.terraskillspowers.assessment.result", candidates, radius), false);
    }

    @SubscribeEvent
    public static void onPlayerTick(net.neoforged.neoforge.event.tick.PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // Tool swaps are observed within one tick; rank changes are checked alongside the existing one-second sync.
            ExcavationReach.reconcile(player, player.tickCount % 20 == 0);
            if (player.tickCount % 20 == 0) syncRanks(player);
            if (player.tickCount % 5 == 0) syncHandcartWeight(player);
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ExcavationReach.clear(player);
            ExcavationReach.reconcile(player, true);
            syncRanks(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ExcavationReach.clear(player);
            if (event.getOriginal() instanceof ServerPlayer original) ExcavationReach.clear(original);
            BrutishMining.clear(player.getUUID());
            MiningDurability.clear(player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        LAST_ASSESSMENT.remove(event.getEntity().getUUID());
        ExcavationReach.forget(event.getEntity().getUUID());
        BrutishMining.clear(event.getEntity().getUUID());
        MiningDurability.clear(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        LAST_ASSESSMENT.clear();
        ExcavationReach.clearAll();
        BrutishMining.clearAll();
        MiningDurability.clearAll();
    }

    public static void syncRanks(ServerPlayer player) {
        int brutishRank = BrutishMining.rank(player);
        PacketDistributor.sendToPlayer(player, new SkillRanksPayload(SkillRanks.mining(player), SkillRanks.deepMining(player),
                SkillRanks.stoneMining(player), SkillRanks.excavating(player), brutishRank, BrutishMining.isActive(player)));
    }

    private static void syncHandcartWeight(ServerPlayer player) {
        TFCHandCartEntity handcart = findPulledHandcart(player);
        if (handcart == null) {
            PacketDistributor.sendToPlayer(player, new HandcartWeightPayload(false, 0.0D, 0.0D, 0.0D));
            return;
        }

        double cartWeightKg = cartWeightKg(handcart);
        double normalLimitKg = normalHandcartLimitKg(player);
        PacketDistributor.sendToPlayer(player, new HandcartWeightPayload(true, cartWeightKg, normalLimitKg,
                normalLimitKg * HANDCART_OVERLOAD_ALLOWANCE));
    }

    private static TFCHandCartEntity findPulledHandcart(ServerPlayer player) {
        return player.serverLevel().getEntitiesOfClass(TFCHandCartEntity.class,
                player.getBoundingBox().inflate(8.0D), cart -> cart.getPulling() == player).stream().findFirst().orElse(null);
    }

    private static double cartWeightKg(TFCHandCartEntity handcart) {
        double cartWeightKg = 0.0D;
        for (int slot = 0; slot < handcart.getContainerSize(); slot++) {
            cartWeightKg += WeightViews1211.weightOf(handcart.getItem(slot));
        }
        return cartWeightKg;
    }

    private static double normalHandcartLimitKg(ServerPlayer player) {
        double capacityKg = UltimateWeightCommon.bootstrap().constraintEvaluator()
                .resolveCarryCapacityKg(WeightViews1211.player(player));
        double normalCarryCapacityKg = capacityKg * SOFT_CARRY_CAPACITY_MULTIPLIER;
        return Math.max(0.0D, normalCarryCapacityKg - WeightViews1211.totalWeight(player))
                * HANDCART_CAPACITY_MULTIPLIER;
    }

    private record AssessmentCooldown(ResourceKey<net.minecraft.world.level.Level> dimension, long gameTime) {}
}
