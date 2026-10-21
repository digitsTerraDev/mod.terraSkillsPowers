package com.terraskills.powers.exploration;

import com.terraskills.powers.config.PowersConfig;
import com.terraskills.powers.skill.SkillRanks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks the source of TFC exhaustion until TFC turns a four-point exhaustion unit into thirst. This deliberately
 * changes that conversion instead of trying to refund thirst after the fact, where heat, food, and healing sources
 * can no longer be distinguished.
 */
public final class TravelThirstLedger {
    private static final ThreadLocal<ServerPlayer> TRAVEL_CONTEXT = new ThreadLocal<>();
    private static final Map<UUID, ArrayDeque<Segment>> SEGMENTS = new HashMap<>();

    private TravelThirstLedger() {}

    public static void beginMovement(ServerPlayer player) { TRAVEL_CONTEXT.set(player); }
    public static void endMovement() { TRAVEL_CONTEXT.remove(); }

    /** PlayerInfo forwards 40% of its argument into the native food exhaustion buffer. */
    public static void record(Player player, float playerInfoExhaustion) {
        if (!(player instanceof ServerPlayer server) || playerInfoExhaustion <= 0.0F) return;
        boolean travel = TRAVEL_CONTEXT.get() == server;
        ArrayDeque<Segment> segments = SEGMENTS.computeIfAbsent(server.getUUID(), ignored -> new ArrayDeque<>());
        double amount = playerInfoExhaustion * 0.4D;
        Segment tail = segments.peekLast();
        if (tail != null && tail.travel() == travel) {
            segments.removeLast();
            segments.addLast(new Segment(tail.amount() + amount, travel));
        } else {
            segments.addLast(new Segment(amount, travel));
        }
    }

    /**
     * TFC's normal-difficulty thirst loss is base * (1 + temperatureContribution). Only the movement share of the
     * base is discounted, retaining the temperature component exactly.
     */
    public static float discountTravelThirst(ServerPlayer player, float originalLoss, float temperatureContribution) {
        if (!(originalLoss < 0.0F)) return originalLoss;
        int rank = Math.clamp(SkillRanks.waterConservation(player), 0, 5);
        double movementFraction = consumeTravelFraction(player.getUUID(), 4.0D);
        if (rank == 0 || movementFraction <= 0.0D) return originalLoss;
        double multiplier = Math.max(1.0D, 1.0D + temperatureContribution);
        double reduction = Math.clamp(PowersConfig.WATER_CONSERVATION_TRAVEL_THIRST_REDUCTION_PER_RANK.get() * rank,
                0.0D, 1.0D);
        double discount = -originalLoss / multiplier * movementFraction * reduction;
        return (float) Math.min(0.0D, originalLoss + discount);
    }

    public static void clear(UUID playerId) { SEGMENTS.remove(playerId); }
    public static void clearAll() { SEGMENTS.clear(); TRAVEL_CONTEXT.remove(); }

    private static double consumeTravelFraction(UUID playerId, double amount) {
        ArrayDeque<Segment> segments = SEGMENTS.get(playerId);
        if (segments == null || segments.isEmpty()) return 0.0D;
        double remaining = amount;
        double travel = 0.0D;
        while (remaining > 0.000001D && !segments.isEmpty()) {
            Segment head = segments.removeFirst();
            double used = Math.min(remaining, head.amount());
            if (head.travel()) travel += used;
            remaining -= used;
            if (head.amount() > used) segments.addFirst(new Segment(head.amount() - used, head.travel()));
        }
        if (segments.isEmpty()) SEGMENTS.remove(playerId);
        // Missing history is intentionally non-travel: an old/external exhaustion unit cannot be safely discounted.
        return travel / amount;
    }

    private record Segment(double amount, boolean travel) {}
}
