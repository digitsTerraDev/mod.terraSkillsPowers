package com.terraskills.powers.horse;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Shared per-process state used by the horse movement mixins on either game side. */
public final class HorseLoadState {
    private static final Map<UUID, Restrictions> RESTRICTIONS = new ConcurrentHashMap<>();

    private HorseLoadState() {}

    public static void set(UUID horseId, boolean jumpBlocked, boolean noStepAssist, boolean movementBlocked,
                           double speedMultiplier) {
        RESTRICTIONS.put(horseId, new Restrictions(jumpBlocked, noStepAssist, movementBlocked, speedMultiplier));
    }

    public static void clear(UUID horseId) {
        RESTRICTIONS.remove(horseId);
    }

    public static boolean isJumpBlocked(UUID horseId) {
        return RESTRICTIONS.getOrDefault(horseId, Restrictions.NONE).jumpBlocked();
    }

    public static boolean hasNoStepAssist(UUID horseId) {
        return RESTRICTIONS.getOrDefault(horseId, Restrictions.NONE).noStepAssist();
    }

    public static boolean isMovementBlocked(UUID horseId) {
        return RESTRICTIONS.getOrDefault(horseId, Restrictions.NONE).movementBlocked();
    }

    public static double speedMultiplier(UUID horseId) {
        return RESTRICTIONS.getOrDefault(horseId, Restrictions.NONE).speedMultiplier();
    }

    private record Restrictions(boolean jumpBlocked, boolean noStepAssist, boolean movementBlocked, double speedMultiplier) {
        private static final Restrictions NONE = new Restrictions(false, false, false, 1.0D);
    }
}
