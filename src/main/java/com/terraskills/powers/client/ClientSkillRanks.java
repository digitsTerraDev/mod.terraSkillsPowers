package com.terraskills.powers.client;

import com.terraskills.powers.network.SkillRanksPayload;

/** Client mirror used only for break-speed prediction; the server always re-reads Pufferfish state. */
public final class ClientSkillRanks {
    private static volatile int mining, deepMining, stoneMining, excavating;

    private ClientSkillRanks() {}

    public static void accept(SkillRanksPayload payload) {
        mining = payload.mining();
        deepMining = payload.deepMining();
        stoneMining = payload.stoneMining();
        excavating = payload.excavating();
    }

    public static int mining() { return mining; }
    public static int deepMining() { return deepMining; }
    public static int stoneMining() { return stoneMining; }
    public static int excavating() { return excavating; }
}
