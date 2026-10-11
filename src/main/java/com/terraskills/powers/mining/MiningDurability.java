package com.terraskills.powers.mining;

import com.terraskills.powers.skill.SkillRanks;
import net.dries007.tfc.util.Helpers;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

/** Shared entry point for mining-only wear. Conservation rolls before TFC/Unbreaking handle a charge. */
public final class MiningDurability {
    private static final Map<UUID, IdentityHashMap<ItemStack, Double>> FRACTIONAL_WEAR = new java.util.HashMap<>();
    private MiningDurability() {}

    public static void damageForMinedBlock(ItemStack stack, LivingEntity miner, EquipmentSlot slot) {
        if (miner instanceof net.minecraft.server.level.ServerPlayer player && slot == EquipmentSlot.MAINHAND
                && stack == player.getMainHandItem() && (stack.is(ItemTags.PICKAXES) || stack.is(ItemTags.SHOVELS))) {
            int brutishRank = BrutishMining.activeRank(player);
            int rank = SkillRanks.rank(player, "tool_conservation");
            float conservationChance = 0.03F * rank * (brutishRank > 0 ? 2 : 1);
            if (conservationChance > 0 && player.getRandom().nextFloat() < conservationChance) return;

            double charge = brutishRank > 0 ? BrutishMining.wearMultiplier(brutishRank) : 1D;
            IdentityHashMap<ItemStack, Double> pending = FRACTIONAL_WEAR.computeIfAbsent(player.getUUID(), ignored -> new IdentityHashMap<>());
            double accumulated = pending.getOrDefault(stack, 0D) + charge;
            int wholeCharges = (int) Math.floor(accumulated);
            pending.put(stack, accumulated - wholeCharges);
            for (int chargeIndex = 0; chargeIndex < wholeCharges && !stack.isEmpty(); chargeIndex++) {
                Helpers.damageItem(stack, miner, slot);
            }
            return;
        }
        Helpers.damageItem(stack, miner, slot);
    }

    public static void clear(UUID playerId) {
        FRACTIONAL_WEAR.remove(playerId);
    }

    public static void clearAll() {
        FRACTIONAL_WEAR.clear();
    }
}
