package com.terraskills.powers.event;

import com.terraskills.powers.TerraSkillsPowers;
import com.terraskills.powers.config.PowersConfig;
import com.terraskills.powers.mining.ExcavationReach;
import com.terraskills.powers.mining.BrutishMining;
import com.terraskills.powers.mining.MiningDurability;
import com.terraskills.powers.client.ClientSkillRanks;
import com.terraskills.powers.network.SkillRanksPayload;
import com.terraskills.powers.network.HandcartWeightPayload;
import com.terraskills.powers.network.HorseWeightPayload;
import com.terraskills.powers.horse.HorseLoadState;
import com.terraskills.powers.weight.WeightSkillPowers;
import com.terraskills.powers.weight.MountAttributePowers;
import com.terraskills.powers.skill.SkillRanks;
import com.terraskills.powers.exploration.TrailActivityPowers;
import com.terraskills.powers.exploration.TravelThirstLedger;
import com.terraskills.powers.exploration.ActivityHeatmapService;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.warfactory.ultimateweight.UltimateWeightCommon;
import com.warfactory.ultimateweight.v1211.WeightViews1211;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.data.Support;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import tfcastikorcarts.common.entities.carts.TFCHandCartEntity;
import tfcastikorcarts.common.entities.carts.TFCAbstractDrawnInventoryEntity;
import tfcastikorcarts.common.entities.carts.TFCAbstractDrawnEntity;
import tfcastikorcarts.common.entities.carts.TFCAnimalCartEntity;


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
    private static final Map<UUID, UUID> PLAYER_RESTRICTED_HORSES = new HashMap<>();
    private static final Map<UUID, UUID> PLAYER_STEP_HEIGHT_HORSES = new HashMap<>();
    private PowersEvents() {}

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("terraskillspowers")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("debug")
                        .then(Commands.literal("activity_heatmap")
                                .then(Commands.literal("enable")
                                        .executes(context -> setActivityHeatmapEnabled(context.getSource(), true)))
                                .then(Commands.literal("disable")
                                        .executes(context -> setActivityHeatmapEnabled(context.getSource(), false))))));
    }

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
        if (event.getEntity() instanceof AbstractHorse horse
                && horse.getControllingPassenger() instanceof ServerPlayer player
                && findHorseCart(player, horse) instanceof TFCAbstractDrawnEntity cart
                && horseLoadKg(horse, cart) > horseCapacityKg(horse) * horseJumpLoadLimit(horse)) {
            horse.setDeltaMovement(horse.getDeltaMovement().x, 0.0D, horse.getDeltaMovement().z);
            horse.hurtMarked = true;
            return;
        }

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
            MountAttributePowers.reconcile(player);
            TrailActivityPowers.tick(player);
            updateHorseStepAssist(player);
            if (player.tickCount % 20 == 0) syncRanks(player);
            if (player.tickCount % 5 == 0) {
                syncHandcartWeight(player);
                syncHorseWeight(player);
            }
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ExcavationReach.clear(player);
            ExcavationReach.reconcile(player, true);
            TrailActivityPowers.clear(player);
            TrailActivityPowers.reconcile(player);
            ActivityHeatmapService.setEnabled(player, false);
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
            MountAttributePowers.clear(player);
            TrailActivityPowers.clear(player);
            TravelThirstLedger.clear(player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        LAST_ASSESSMENT.remove(event.getEntity().getUUID());
        ExcavationReach.forget(event.getEntity().getUUID());
        BrutishMining.clear(event.getEntity().getUUID());
        MiningDurability.clear(event.getEntity().getUUID());
        TrailActivityPowers.forget(event.getEntity().getUUID());
        ActivityHeatmapService.forget(event.getEntity().getUUID());
        TravelThirstLedger.clear(event.getEntity().getUUID());
        if (event.getEntity() instanceof ServerPlayer player) MountAttributePowers.clear(player);
        restoreHorseStepAssist(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        LAST_ASSESSMENT.clear();
        ExcavationReach.clearAll();
        BrutishMining.clearAll();
        MiningDurability.clearAll();
        MountAttributePowers.clearAll();
        TrailActivityPowers.clearAll();
        ActivityHeatmapService.clearAll();
        TravelThirstLedger.clearAll();
        PLAYER_RESTRICTED_HORSES.values().forEach(HorseLoadState::clear);
        PLAYER_RESTRICTED_HORSES.clear();
        PLAYER_STEP_HEIGHT_HORSES.clear();
    }

    public static void syncRanks(ServerPlayer player) {
        int brutishRank = BrutishMining.rank(player);
        PacketDistributor.sendToPlayer(player, new SkillRanksPayload(SkillRanks.mining(player), SkillRanks.deepMining(player),
                SkillRanks.stoneMining(player), SkillRanks.excavating(player), brutishRank, BrutishMining.isActive(player),
                SkillRanks.trailblazing(player), SkillRanks.wellTrodden(player), SkillRanks.scrambling(player),
                SkillRanks.expeditionPacking(player)));
    }

    private static int setActivityHeatmapEnabled(CommandSourceStack source, boolean enabled) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ActivityHeatmapService.setEnabled(player, enabled);
        source.sendSuccess(() -> Component.translatable(enabled
                ? "command.terraskillspowers.activity_heatmap.enabled"
                : "command.terraskillspowers.activity_heatmap.disabled"), false);
        return 1;
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

    private static void syncHorseWeight(ServerPlayer player) {
        HorseCartContext context = findHorseCartContext(player);
        if (context == null) {
            PacketDistributor.sendToPlayer(player, emptyHorseWeight());
            return;
        }
        double loadKg = horseLoadKg(context.horse(), context.cart());
        double capacityKg = horseCapacityKg(context.horse());
        HorseBackLoad backLoad = horseBackLoad(context.horse());
        double jumpLimit = horseJumpLoadLimit(context.horse());
        double stepLimit = horseStepLoadLimit(context.horse());
        PacketDistributor.sendToPlayer(player, new HorseWeightPayload(true, context.horse().getUUID(), loadKg, capacityKg,
                backLoad.weightKg(), backLoad.capacityKg(),
                loadKg > capacityKg * jumpLimit || backLoad.weightKg() > backLoad.capacityKg() * jumpLimit,
                loadKg > capacityKg * stepLimit || backLoad.weightKg() > backLoad.capacityKg() * stepLimit,
                loadKg > capacityKg || backLoad.weightKg() > backLoad.capacityKg(),
                horseSpeedMultiplier(context.horse(), loadKg, capacityKg)));
    }

    private static TFCAbstractDrawnEntity findHorseCart(ServerPlayer player, AbstractHorse horse) {
        return player.serverLevel().getEntitiesOfClass(TFCAbstractDrawnEntity.class,
                horse.getBoundingBox().inflate(12.0D), cart -> cart.getPulling() == horse).stream().findFirst().orElse(null);
    }

    private static HorseCartContext findHorseCartContext(ServerPlayer player) {
        if (player.getVehicle() instanceof AbstractHorse horse) {
            return new HorseCartContext(horse, findHorseCart(player, horse));
        }
        if (player.getVehicle() instanceof TFCAnimalCartEntity cart && cart.getPulling() instanceof AbstractHorse horse) {
            return new HorseCartContext(horse, cart);
        }
        return null;
    }

    private static double cartWeightKg(TFCAbstractDrawnInventoryEntity cart) {
        double total = 0.0D;
        for (int slot = 0; slot < cart.getContainerSize(); slot++) {
            total += WeightViews1211.weightOf(cart.getItem(slot));
        }
        return total;
    }

    private static double horseLoadKg(AbstractHorse horse, TFCAbstractDrawnEntity cart) {
        Player rider = horse.getControllingPassenger() instanceof Player player ? player : null;
        double total = cart instanceof TFCAbstractDrawnInventoryEntity inventoryCart ? cartWeightKg(inventoryCart) : 0.0D;
        if (rider != null) total += WeightSkillPowers.effectiveMuleWeightKg(horse, rider, WeightViews1211.totalWeight(rider));
        if (cart != null) {
            for (Entity passenger : cart.getPassengers()) {
                if (passenger instanceof Player player) {
                    total += WeightSkillPowers.effectiveMuleWeightKg(horse, rider, WeightViews1211.totalWeight(player));
                }
            }
        }
        return total;
    }

    private static void updateHorseStepAssist(ServerPlayer player) {
        UUID playerId = player.getUUID();
        if (player.getVehicle() instanceof AbstractHorse horse) {
            TFCAbstractDrawnEntity cart = findHorseCart(player, horse);
            UUID horseId = horse.getUUID();
            UUID previousHorseId = PLAYER_RESTRICTED_HORSES.put(playerId, horseId);
            if (previousHorseId != null && !previousHorseId.equals(horseId)) HorseLoadState.clear(previousHorseId);
            double loadKg = horseLoadKg(horse, cart);
            double capacityKg = horseCapacityKg(horse);
            HorseBackLoad backLoad = horseBackLoad(horse);
            double jumpLimit = horseJumpLoadLimit(horse);
            double stepLimit = horseStepLoadLimit(horse);
            HorseLoadState.set(horseId,
                    loadKg > capacityKg * jumpLimit || backLoad.weightKg() > backLoad.capacityKg() * jumpLimit,
                    loadKg > capacityKg * stepLimit || backLoad.weightKg() > backLoad.capacityKg() * stepLimit,
                    loadKg > capacityKg || backLoad.weightKg() > backLoad.capacityKg(),
                    horseSpeedMultiplier(horse, loadKg, capacityKg));
            return;
        }
        UUID horseId = PLAYER_RESTRICTED_HORSES.remove(playerId);
        if (horseId != null) HorseLoadState.clear(horseId);
    }

    private static void restoreHorseStepAssist(UUID playerId) {
        UUID horseId = PLAYER_RESTRICTED_HORSES.remove(playerId);
        if (horseId != null) HorseLoadState.clear(horseId);
    }

    private static HorseWeightPayload emptyHorseWeight() {
        return new HorseWeightPayload(false, new UUID(0L, 0L), 0.0D, 0.0D, 0.0D, 0.0D,
                false, false, false, 1.0D);
    }

    private static HorseBackLoad horseBackLoad(AbstractHorse horse) {
        if (!(horse.getControllingPassenger() instanceof Player rider)) return new HorseBackLoad(0.0D, 0.0D);
        double capacityKg = WeightSkillPowers.horseBackCapacityKg(horse, rider,
                UltimateWeightCommon.bootstrap().config().defaultCarryCapacityKg());
        double armorWeightKg = WeightSkillPowers.effectiveArmorWeightKg(rider,
                WeightViews1211.weightOf(horse.getItemBySlot(EquipmentSlot.BODY)));
        double totalWeightKg = armorWeightKg;
        for (Entity passenger : horse.getPassengers()) {
            if (passenger instanceof Player player) totalWeightKg += WeightViews1211.totalWeight(player);
        }
        return new HorseBackLoad(WeightSkillPowers.effectiveMuleWeightKg(horse, rider, totalWeightKg), capacityKg);
    }

    private static double horseCapacityKg(AbstractHorse horse) {
        double jumpStrength = horse.getAttributeValue(Attributes.JUMP_STRENGTH);
        double minJumpStrength = PowersConfig.PULL_CAPACITY_MIN_JUMP_STRENGTH.get();
        double maxJumpStrength = PowersConfig.PULL_CAPACITY_MAX_JUMP_STRENGTH.get();
        double progress = Math.clamp((jumpStrength - minJumpStrength) / Math.max(0.0001D, maxJumpStrength - minJumpStrength), 0.0D, 1.0D);
        double minCapacityKg = horse instanceof net.minecraft.world.entity.animal.horse.Donkey ? PowersConfig.DONKEY_MIN_PULL_CAPACITY_KG.get()
                : horse instanceof net.minecraft.world.entity.animal.horse.Mule ? PowersConfig.MULE_MIN_PULL_CAPACITY_KG.get()
                : PowersConfig.HORSE_MIN_PULL_CAPACITY_KG.get();
        double maxCapacityKg = horse instanceof net.minecraft.world.entity.animal.horse.Donkey ? PowersConfig.DONKEY_MAX_PULL_CAPACITY_KG.get()
                : horse instanceof net.minecraft.world.entity.animal.horse.Mule ? PowersConfig.MULE_MAX_PULL_CAPACITY_KG.get()
                : PowersConfig.HORSE_MAX_PULL_CAPACITY_KG.get();
        double baseCapacityKg = minCapacityKg + progress * (maxCapacityKg - minCapacityKg);
        Player rider = horse.getControllingPassenger() instanceof Player player ? player : null;
        return WeightSkillPowers.horsePullCapacityKg(horse, rider, baseCapacityKg);
    }

    private static double horseJumpLoadLimit(AbstractHorse horse) {
        Player rider = horse.getControllingPassenger() instanceof Player player ? player : null;
        return WeightSkillPowers.jumpLoadLimit(horse, rider, baseJumpLoadLimit(horse));
    }

    private static double horseStepLoadLimit(AbstractHorse horse) {
        Player rider = horse.getControllingPassenger() instanceof Player player ? player : null;
        return WeightSkillPowers.stepLoadLimit(horse, rider, baseStepLoadLimit(horse));
    }

    private static double horseSpeedMultiplier(AbstractHorse horse, double loadKg, double capacityKg) {
        Player rider = horse.getControllingPassenger() instanceof Player player ? player : null;
        double loadRatio = capacityKg > 0.0D ? loadKg / capacityKg : Double.POSITIVE_INFINITY;
        return WeightSkillPowers.speedMultiplier(horse, rider, loadRatio);
    }

    private static double baseJumpLoadLimit(AbstractHorse horse) {
        if (horse instanceof net.minecraft.world.entity.animal.horse.Donkey) return PowersConfig.DONKEY_JUMP_LOAD_LIMIT.get();
        if (horse instanceof net.minecraft.world.entity.animal.horse.Mule) return PowersConfig.MULE_JUMP_LOAD_LIMIT.get();
        return PowersConfig.HORSE_JUMP_LOAD_LIMIT.get();
    }

    private static double baseStepLoadLimit(AbstractHorse horse) {
        if (horse instanceof net.minecraft.world.entity.animal.horse.Donkey) return PowersConfig.DONKEY_STEP_LOAD_LIMIT.get();
        if (horse instanceof net.minecraft.world.entity.animal.horse.Mule) return PowersConfig.MULE_STEP_LOAD_LIMIT.get();
        return PowersConfig.HORSE_STEP_LOAD_LIMIT.get();
    }

    private record AssessmentCooldown(ResourceKey<net.minecraft.world.level.Level> dimension, long gameTime) {}

    private record HorseCartContext(AbstractHorse horse, TFCAbstractDrawnEntity cart) {}

    private record HorseBackLoad(double weightKg, double capacityKg) {}

}
