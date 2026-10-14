package com.terraskills.powers.client;

import com.terraskills.powers.network.HandcartWeightPayload;

/** Client cache of the server-authoritative handcart HUD values. */
public final class ClientHandcartWeight {
    private static Snapshot current;

    private ClientHandcartWeight() {}

    public static void accept(HandcartWeightPayload payload) {
        current = payload.attached()
                ? new Snapshot(payload.cartWeightKg(), payload.normalLimitKg(), payload.hardLimitKg())
                : null;
    }

    public static void clear() {
        current = null;
    }

    public static Snapshot current() {
        return current;
    }

    public record Snapshot(double cartWeightKg, double normalLimitKg, double hardLimitKg) {}
}
