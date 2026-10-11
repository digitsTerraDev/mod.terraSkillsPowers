package com.terraskills.powers.event;

import com.terraskills.powers.TerraSkillsPowers;
import com.terraskills.powers.mining.CarefulEyeApi;
import com.terraskills.powers.mining.CarefulEyeProvenance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.PistonEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.HashSet;
import java.util.Set;

@EventBusSubscriber(modid = TerraSkillsPowers.MOD_ID)
public final class CarefulEyeEvents {
    private static final Set<DiscoveryPosition> DISCOVERIES_THIS_TICK = new HashSet<>();

    private CarefulEyeEvents() {}

    @SubscribeEvent
    public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel() instanceof ServerLevel level
                && event.getPlacedBlock().is(net.dries007.tfc.common.TFCTags.Blocks.STONES_RAW)) {
            CarefulEyeProvenance.get(level).markPlaced(event.getPos());
        }
    }

    @SubscribeEvent
    public static void onPistonMove(PistonEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        for (BlockPos source : event.getStructureHelper().getToPush()) {
            BlockPos destination = source.relative(event.getDirection());
            // Conservatively exclude moved raw stone: provenance cannot be safely transferred across every mover API.
            if (level.getBlockState(destination).is(net.dries007.tfc.common.TFCTags.Blocks.STONES_RAW)) {
                CarefulEyeProvenance.get(level).markPlaced(destination);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBreak(BlockEvent.BreakEvent event) {
        if (event.isCanceled() || !(event.getPlayer() instanceof ServerPlayer player) || !(event.getLevel() instanceof ServerLevel level)) return;
        BlockPos pos = event.getPos();
        DiscoveryPosition discovery = new DiscoveryPosition(level.dimension(), pos.asLong());
        if (!DISCOVERIES_THIS_TICK.add(discovery)) return;
        CarefulEyeApi.tryDiscover(player, level, pos, event.getState()).ifPresent(stack -> {
            if (!player.addItem(stack)) player.drop(stack, false);
        });
    }

    @SubscribeEvent
    public static void endServerTick(ServerTickEvent.Post event) {
        DISCOVERIES_THIS_TICK.clear();
    }

    private record DiscoveryPosition(ResourceKey<Level> dimension, long position) {}
}
