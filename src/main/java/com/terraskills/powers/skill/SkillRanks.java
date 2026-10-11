package com.terraskills.powers.skill;

import com.terraskills.powers.client.ClientSkillRanks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.puffish.skillsmod.api.Skill;
import net.puffish.skillsmod.api.SkillsAPI;

/** Reads unlocked Pufferfish nodes. No rank is cached on the server, so refunds and resets apply immediately. */
public final class SkillRanks {
    public static final ResourceLocation MINER = ResourceLocation.fromNamespaceAndPath("terraskills", "miner");

    private SkillRanks() {}

    public static int rank(ServerPlayer player, String... prefixes) {
        return SkillsAPI.getCategory(MINER).map(category -> {
            for (String prefix : prefixes) {
                for (int rank = 5; rank >= 1; rank--) {
                    if (category.getSkill(prefix + "_" + rank)
                            .map(skill -> skill.getState(player) == Skill.State.UNLOCKED).orElse(false)) {
                        return rank;
                    }
                }
            }
            return 0;
        }).orElse(0);
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
}
