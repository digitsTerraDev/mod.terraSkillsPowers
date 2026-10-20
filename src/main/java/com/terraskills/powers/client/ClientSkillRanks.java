package com.terraskills.powers.client;

import com.terraskills.powers.network.SkillRanksPayload;

/** Client mirror used only for break-speed prediction; the server always re-reads Pufferfish state. */
public final class ClientSkillRanks {
    private static volatile int mining, deepMining, stoneMining, excavating, brutishMining;
    private static volatile int trailblazing, wellTrodden, scrambling, expeditionPacking;
    private static volatile boolean brutishActive;

    private ClientSkillRanks() {}

    public static void accept(SkillRanksPayload payload) {
        mining = payload.mining();
        deepMining = payload.deepMining();
        stoneMining = payload.stoneMining();
        excavating = payload.excavating();
        brutishMining = payload.brutishMining();
        brutishActive = payload.brutishActive();
        trailblazing = payload.trailblazing();
        wellTrodden = payload.wellTrodden();
        scrambling = payload.scrambling();
        expeditionPacking = payload.expeditionPacking();
    }

    public static int mining() { return mining; }
    public static int deepMining() { return deepMining; }
    public static int stoneMining() { return stoneMining; }
    public static int excavating() { return excavating; }
    public static int brutishMining() { return brutishMining; }
    public static boolean brutishActive() { return brutishActive; }
    public static int trailblazing() { return trailblazing; }
    public static int wellTrodden() { return wellTrodden; }
    public static int scrambling() { return scrambling; }
    public static int expeditionPacking() { return expeditionPacking; }

    public static void clear() {
        mining = deepMining = stoneMining = excavating = brutishMining = 0;
        trailblazing = wellTrodden = scrambling = expeditionPacking = 0;
        brutishActive = false;
    }
}
