package com.terraskills.powers.mining;

import com.terraskills.powers.skill.SkillRanks;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** Server-authoritative active state for Brutish Mining. It is intentionally session-only. */
public final class BrutishMining {
    private static final Set<UUID> ACTIVE_PLAYERS = new HashSet<>();

    private BrutishMining() {}

    public static boolean toggle(ServerPlayer player) {
        if (rank(player) == 0) {
            ACTIVE_PLAYERS.remove(player.getUUID());
            return false;
        }
        if (!ACTIVE_PLAYERS.add(player.getUUID())) ACTIVE_PLAYERS.remove(player.getUUID());
        return ACTIVE_PLAYERS.contains(player.getUUID());
    }

    public static boolean isActive(ServerPlayer player) {
        if (rank(player) == 0) {
            ACTIVE_PLAYERS.remove(player.getUUID());
            return false;
        }
        return ACTIVE_PLAYERS.contains(player.getUUID());
    }

    public static int rank(ServerPlayer player) {
        return SkillRanks.mining(player) >= 4 ? SkillRanks.rank(player, "brutish_mining") : 0;
    }

    public static int activeRank(ServerPlayer player) {
        return isActive(player) ? rank(player) : 0;
    }

    public static double wearMultiplier(int rank) {
        return 2.5D + 0.5D * rank;
    }

    public static void clear(UUID playerId) {
        ACTIVE_PLAYERS.remove(playerId);
    }

    public static void clearAll() {
        ACTIVE_PLAYERS.clear();
    }
}
