package com.terraskills.powers.client;

import com.terraskills.powers.network.SkillRanksPayload;

/** Client mirror used only for break-speed prediction; the server always re-reads Pufferfish state. */
public final class ClientSkillRanks {
    private static volatile int mining, deepMining, stoneMining, excavating, brutishMining;
    private static volatile boolean brutishActive;

    private ClientSkillRanks() {}

    public static void accept(SkillRanksPayload payload) {
        mining = payload.mining();
        deepMining = payload.deepMining();
        stoneMining = payload.stoneMining();
        excavating = payload.excavating();
        brutishMining = payload.brutishMining();
        brutishActive = payload.brutishActive();
    }

    public static int mining() { return mining; }
    public static int deepMining() { return deepMining; }
    public static int stoneMining() { return stoneMining; }
    public static int excavating() { return excavating; }
    public static int brutishMining() { return brutishMining; }
    public static boolean brutishActive() { return brutishActive; }

    public static void clear() {
        mining = deepMining = stoneMining = excavating = brutishMining = 0;
        brutishActive = false;
    }
}
