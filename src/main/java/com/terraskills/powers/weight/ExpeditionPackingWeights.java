package com.terraskills.powers.weight;

import com.terraskills.powers.TerraSkillsPowers;
import com.terraskills.powers.config.PowersConfig;
import com.terraskills.powers.skill.SkillRanks;
import com.warfactory.ultimateweight.api.WeightStackView;
import com.warfactory.ultimateweight.v1211.WeightViews1211;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Per-player effective-weight context shared by Ultimate Weight's client and server total-weight calls. The tag is
 * deliberately empty in this mod: a pack/datapack must opt specific navigation and light supplies in explicitly.
 */
public final class ExpeditionPackingWeights {
    public static final TagKey<Item> EXPLORATION_SUPPLIES = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(TerraSkillsPowers.MOD_ID, "exploration_supplies"));

    private ExpeditionPackingWeights() {}

    public static double discountFor(Player player, ItemStack stack, int resolutionDepth) {
        if (stack == null || stack.isEmpty()) return 0.0D;
        return discountPerItem(player, stack, resolutionDepth) * stack.getCount();
    }

    public static double discountPerItem(Player player, ItemStack stack, int resolutionDepth) {
        if (player == null || stack == null || stack.isEmpty()) return 0.0D;
        int rank = Math.clamp(SkillRanks.expeditionPacking(player), 0, 5);
        if (rank == 0 || !stack.is(EXPLORATION_SUPPLIES)) return 0.0D;
        // Ultimate Weight calculates nested contents separately. Discount only this tagged shell's configured weight;
        // content weight remains untouched and nested containers cannot be discounted twice.
        double shellKg = Math.max(0.0D, WeightViews1211.configuredWeightOf(stack, resolutionDepth));
        double reduction = Math.clamp(PowersConfig.EXPEDITION_PACKING_WEIGHT_REDUCTION_PER_RANK.get() * rank,
                0.0D, 1.0D);
        return shellKg * reduction;
    }

    public static double effectiveSingleWeightKg(Player player, ItemStack stack, int resolutionDepth, double vanillaSingleKg) {
        return Math.max(0.0D, vanillaSingleKg - discountPerItem(player, stack, resolutionDepth));
    }

    public static double effectiveStackWeightKg(Player player, ItemStack stack) {
        return Math.max(0.0D, WeightViews1211.weightOf(stack) - discountFor(player, stack, 0));
    }

    public static WeightStackView wrap(Player player, WeightStackView view) {
        return player == null || view instanceof PlayerOwnedWeightStackView ? view : new PlayerOwnedWeightStackView(player, view);
    }
}
