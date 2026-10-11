package com.terraskills.powers.mining;

import com.terraskills.powers.config.PowersConfig;
import com.terraskills.powers.skill.SkillRanks;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Public server-side boundary for standard and future survey-area gem discoveries. */
public final class CarefulEyeApi {
    private CarefulEyeApi() {}

    public static Optional<ItemStack> tryDiscover(ServerPlayer player, ServerLevel level, BlockPos pos, BlockState state) {
        if (player.isCreative() || !state.is(net.dries007.tfc.common.TFCTags.Blocks.STONES_RAW)
                || !player.getMainHandItem().is(ItemTags.PICKAXES) || !player.getMainHandItem().isCorrectToolForDrops(state)) {
            return Optional.empty();
        }
        if (CarefulEyeProvenance.get(level).consumePlaced(pos)) return Optional.empty();
        int rank = SkillRanks.rank(player, "careful_eye");
        if (rank == 0 || player.getRandom().nextFloat() >= 0.002F * rank) {
            return Optional.empty();
        }

        Rock rock = rockFor(state);
        if (rock == null) return Optional.empty();
        List<WeightedGem> choices = weightedGems(rock);
        int totalWeight = choices.stream().mapToInt(WeightedGem::weight).sum();
        if (totalWeight == 0) return Optional.empty();
        int roll = player.getRandom().nextInt(totalWeight);
        for (WeightedGem choice : choices) {
            roll -= choice.weight;
            if (roll < 0) return Optional.of(new ItemStack(TFCItems.GEMS.get(choice.gem).asItem()));
        }
        return Optional.empty();
    }

    private static Rock rockFor(BlockState state) {
        for (Rock rock : Rock.values()) {
            if (rock.getBlock(Rock.BlockType.RAW).get() == state.getBlock()) return rock;
        }
        return null;
    }

    private static List<WeightedGem> weightedGems(Rock rock) {
        String wantedRock = rock.getSerializedName();
        List<WeightedGem> gems = new ArrayList<>();
        for (String configured : PowersConfig.CAREFUL_EYE_GEMS.get()) {
            String[] association = configured.split("=", 2);
            if (association.length != 2 || !association[0].trim().equalsIgnoreCase(wantedRock)) continue;
            for (String value : association[1].split(",")) {
                String[] parts = value.trim().split("@", 2);
                try {
                    Ore gem = Ore.valueOf(parts[0].trim().toUpperCase(java.util.Locale.ROOT));
                    int weight = parts.length == 2 ? Integer.parseInt(parts[1].trim()) : 1;
                    if (gem.isGem() && weight > 0 && TFCItems.GEMS.containsKey(gem)) gems.add(new WeightedGem(gem, weight));
                } catch (IllegalArgumentException ignored) {
                    // Invalid server-config values are ignored so one typo never blocks mining.
                }
            }
        }
        return gems;
    }

    private record WeightedGem(Ore gem, int weight) {}
}
