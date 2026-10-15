package com.terraskills.powers.skill;

import com.terraskills.powers.client.ClientSkillRanks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.puffish.skillsmod.api.Skill;
import net.puffish.skillsmod.api.SkillsAPI;

/** Reads unlocked Pufferfish nodes. No rank is cached on the server, so refunds and resets apply immediately. */
public final class SkillRanks {
    private SkillRanks() {}

    public static int rank(ServerPlayer player, String... prefixes) {
        return SkillsAPI.streamCategories().mapToInt(category -> {
            for (String prefix : prefixes) {
                for (int rank = 5; rank >= 1; rank--) {
                    if (category.getSkill(prefix + "_" + rank)
                            .map(skill -> skill.getState(player) == Skill.State.UNLOCKED).orElse(false)) {
                        return rank;
                    }
                }
            }
            return 0;
        }).max().orElse(0);
    }

    public static int mining(Player player) {
        return player instanceof ServerPlayer server ? rank(server, "mining", "miner") : ClientSkillRanks.mining();
    }

    public static int deepMining(Player player) {
        return player instanceof ServerPlayer server ? rank(server, "deep_mining") : ClientSkillRanks.deepMining();
    }

    public static int stoneMining(Player player) {
        return player instanceof ServerPlayer server ? rank(server, "stone_mining", "stoneworking") : ClientSkillRanks.stoneMining();
    }

    public static int excavating(Player player) {
        return player instanceof ServerPlayer server ? rank(server, "excavating", "excavation") : ClientSkillRanks.excavating();
    }

    public static int heightsTraining(ServerPlayer player) {
        // mine_safety is the intentionally preserved legacy ID.
        return rank(player, "heights_training", "mine_safety");
    }

    public static int carryCapacityPercent(ServerPlayer player) {
        return rank(player, "carry_capacity_percent", "weight_capacity_percent");
    }

    public static int carryCapacityFlat(ServerPlayer player) {
        return rank(player, "carry_capacity_flat", "weight_capacity_flat");
    }

    public static int ridingPullPercent(ServerPlayer player) {
        return rank(player, "riding_pull_percent", "horse_pull_percent");
    }

    public static int ridingPullFlat(ServerPlayer player) {
        return rank(player, "riding_pull_flat", "horse_pull_flat");
    }

    public static int ridingSpeedPenalty(ServerPlayer player) {
        return rank(player, "riding_speed_penalty", "horse_speed_penalty");
    }

    public static int packAnimalPullPercent(ServerPlayer player) {
        return rank(player, "pack_animal_pull_percent", "donkey_pull_percent");
    }

    public static int packAnimalPullFlat(ServerPlayer player) {
        return rank(player, "pack_animal_pull_flat", "donkey_pull_flat");
    }

    public static int packAnimalBackPercent(ServerPlayer player) {
        return rank(player, "pack_animal_back_percent", "donkey_back_percent");
    }

    public static int packAnimalBackFlat(ServerPlayer player) {
        return rank(player, "pack_animal_back_flat", "donkey_back_flat");
    }

    public static int packAnimalJumpLimit(ServerPlayer player) {
        return rank(player, "pack_animal_jump_limit", "donkey_jump_limit");
    }

    public static int packAnimalStepLimit(ServerPlayer player) {
        return rank(player, "pack_animal_step_limit", "donkey_step_limit");
    }

    public static int horseLightHarness(ServerPlayer player) {
        return rank(player, "horse_light_harness", "light_harness");
    }

    public static int donkeyDraftHarness(ServerPlayer player) {
        return rank(player, "donkey_draft_harness", "draft_harness");
    }

    public static int muleExpeditionBalance(ServerPlayer player) {
        return rank(player, "mule_expedition_balance", "expedition_balance");
    }

    public static int mountSpeed(ServerPlayer player) { return rank(player, "mount_speed"); }
    public static int horseSpeed(ServerPlayer player) { return rank(player, "horse_speed"); }
    public static int muleSpeed(ServerPlayer player) { return rank(player, "mule_speed"); }
    public static int mountArmor(ServerPlayer player) { return rank(player, "mount_armor"); }
    public static int horseArmor(ServerPlayer player) { return rank(player, "horse_armor"); }
    public static int muleArmor(ServerPlayer player) { return rank(player, "mule_armor"); }
    public static int mountToughness(ServerPlayer player) { return rank(player, "mount_toughness"); }
    public static int horseToughness(ServerPlayer player) { return rank(player, "horse_toughness"); }
    public static int muleToughness(ServerPlayer player) { return rank(player, "mule_toughness"); }
}
