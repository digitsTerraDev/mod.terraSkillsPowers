package com.terraskills.powers.client;

import com.terraskills.powers.network.HorseWeightPayload;
import com.terraskills.powers.horse.HorseLoadState;

import java.util.UUID;

/** Client cache of the current ridden horse's server-authoritative carrying values. */
public final class ClientHorseWeight {
    private static Snapshot current;
    private static UUID currentHorseId;

    private ClientHorseWeight() {}

    public static void accept(HorseWeightPayload payload) {
        if (currentHorseId != null && !currentHorseId.equals(payload.horseId())) HorseLoadState.clear(currentHorseId);
        currentHorseId = payload.riding() ? payload.horseId() : null;
        if (payload.riding()) HorseLoadState.set(payload.horseId(), payload.jumpBlocked(), payload.noStepAssist(), payload.movementBlocked());
        current = payload.riding() ? new Snapshot(payload.loadWeightKg(), payload.capacityKg(),
                payload.backWeightKg(), payload.backCapacityKg()) : null;
    }

    public static void clear() {
        if (currentHorseId != null) HorseLoadState.clear(currentHorseId);
        currentHorseId = null;
        current = null;
    }

    public static Snapshot current() {
        return current;
    }

    public record Snapshot(double loadWeightKg, double capacityKg, double backWeightKg, double backCapacityKg) {}
}
