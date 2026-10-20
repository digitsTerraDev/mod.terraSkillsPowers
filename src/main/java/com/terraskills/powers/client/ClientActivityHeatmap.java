package com.terraskills.powers.client;

import com.terraskills.powers.network.ActivityHeatmapControlPayload;
import com.terraskills.powers.network.ActivityHeatmapPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/** Client-side data cache with no JourneyMap types, allowing the optional renderer to load only when JourneyMap exists. */
public final class ClientActivityHeatmap {
    private static volatile State state = new State(0L, false, null);
    private static int newestRequestId;

    private ClientActivityHeatmap() {}

    public static synchronized void accept(ActivityHeatmapPayload payload) {
        if (!state.enabled()) return;
        if (Integer.compareUnsigned(payload.requestId(), newestRequestId) < 0) return;
        newestRequestId = payload.requestId();
        State previous = state;
        state = new State(previous.revision() + 1L, true, new Snapshot(payload.requestId(), payload.dimension(),
                payload.minChunkX(), payload.minChunkZ(), payload.maxChunkX(), payload.maxChunkZ(), payload.entries()));
    }

    public static synchronized void acceptControl(ActivityHeatmapControlPayload payload) {
        State previous = state;
        if (previous.enabled() == payload.enabled() && previous.snapshot() == null) return;
        newestRequestId = 0;
        state = new State(previous.revision() + 1L, payload.enabled(), null);
    }

    public static synchronized void clear() {
        if (!state.enabled() && state.snapshot() == null) return;
        newestRequestId = 0;
        state = new State(state.revision() + 1L, false, null);
    }

    public static State state() {
        return state;
    }

    public static boolean enabled() {
        return state.enabled();
    }

    public record State(long revision, boolean enabled, Snapshot snapshot) {}

    public record Snapshot(int requestId, ResourceLocation dimension, int minChunkX, int minChunkZ,
                           int maxChunkX, int maxChunkZ, List<ActivityHeatmapPayload.ChunkActivity> entries) {
        public Snapshot {
            entries = List.copyOf(entries);
        }
    }
}
