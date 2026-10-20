package com.terraskills.powers.exploration;

import com.terraskills.powers.config.PowersConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Dimension-local shared travel activity. Entries are just chunk coordinates and timestamps; querying them never
 * touches the chunk manager, so an unloaded area naturally decays without being force-loaded.
 */
public final class AreaActivityData extends SavedData {
    private static final String DATA_ID = "terraskillspowers_area_activity";
    private final Map<Long, Entry> entries = new HashMap<>();

    public static AreaActivityData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(AreaActivityData::new, AreaActivityData::load, DataFixTypes.LEVEL), DATA_ID);
    }

    private static AreaActivityData load(CompoundTag tag, HolderLookup.Provider registries) {
        AreaActivityData data = new AreaActivityData();
        ListTag saved = tag.getList("entries", Tag.TAG_COMPOUND);
        for (int index = 0; index < saved.size(); index++) {
            CompoundTag entry = saved.getCompound(index);
            data.entries.put(entry.getLong("chunk"), new Entry(entry.getDouble("activity"), entry.getLong("updated")));
        }
        return data;
    }

    public void add(long packedChunk, double contribution, long gameTime) {
        if (!(contribution > 0.0D)) return;
        Entry prior = entries.get(packedChunk);
        double activity = prior == null ? 0.0D : decay(prior.activity(), prior.lastUpdate(), gameTime);
        entries.put(packedChunk, new Entry(activity + contribution, gameTime));
        setDirty();
    }

    /** Returns raw decayed activity without mutating or loading anything. */
    public double activity(long packedChunk, long gameTime) {
        Entry entry = entries.get(packedChunk);
        return entry == null ? 0.0D : decay(entry.activity(), entry.lastUpdate(), gameTime);
    }

    public static double normalize(double activity) {
        double low = PowersConfig.TRAIL_ACTIVITY_NORMALIZATION_LOW.get();
        double high = Math.max(low + 0.0001D, PowersConfig.TRAIL_ACTIVITY_NORMALIZATION_HIGH.get());
        return Math.clamp((activity - low) / (high - low), 0.0D, 1.0D);
    }

    /**
     * Returns the strongest normalized activity cells in a requested chunk rectangle. This only looks up SavedData
     * entries for that bounded rectangle and never asks the chunk manager for a chunk, so viewing the heatmap cannot
     * load terrain or become slower as unrelated explored areas accumulate.
     */
    public List<HeatmapEntry> heatmap(int minChunkX, int minChunkZ, int maxChunkX, int maxChunkZ,
                                      long gameTime, int maxEntries) {
        if (minChunkX > maxChunkX || minChunkZ > maxChunkZ || maxEntries <= 0) return List.of();

        int limit = Math.max(1, maxEntries);
        PriorityQueue<HeatmapEntry> selected = new PriorityQueue<>(Comparator.comparingDouble(HeatmapEntry::activity));
        boolean pruned = false;
        for (long chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
            for (long chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
                int x = (int) chunkX;
                int z = (int) chunkZ;
                long packedChunk = pack(x, z);
                Entry saved = entries.get(packedChunk);
                if (saved == null) continue;

                double activity = decay(saved.activity(), saved.lastUpdate(), gameTime);
                if (!(activity > 0.0D) || !Double.isFinite(activity)) {
                    entries.remove(packedChunk);
                    pruned = true;
                    continue;
                }

                float normalized = (float) normalize(activity);
                if (!(normalized > 0.0F)) continue;
                HeatmapEntry candidate = new HeatmapEntry(x, z, normalized);
                if (selected.size() < limit) {
                    selected.add(candidate);
                } else if (candidate.activity() > selected.peek().activity()) {
                    selected.poll();
                    selected.add(candidate);
                }
            }
        }
        if (pruned) setDirty();

        List<HeatmapEntry> result = new ArrayList<>(selected);
        result.sort(Comparator.comparingInt(HeatmapEntry::chunkZ).thenComparingInt(HeatmapEntry::chunkX));
        return result;
    }

    private static double decay(double value, long previous, long now) {
        if (now <= previous || value <= 0.0D) return Math.max(0.0D, value);
        long duration = Math.max(1L, PowersConfig.TRAIL_ACTIVITY_DECAY_TICKS.get());
        return value * Math.max(0.0D, 1.0D - (double) (now - previous) / duration);
    }

    private static long pack(int chunkX, int chunkZ) { return ((long) chunkX << 32) ^ (chunkZ & 0xffffffffL); }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag saved = new ListTag();
        for (Map.Entry<Long, Entry> mapEntry : entries.entrySet()) {
            CompoundTag entry = new CompoundTag();
            entry.putLong("chunk", mapEntry.getKey());
            entry.putDouble("activity", mapEntry.getValue().activity());
            entry.putLong("updated", mapEntry.getValue().lastUpdate());
            saved.add(entry);
        }
        tag.put("entries", saved);
        return tag;
    }

    private record Entry(double activity, long lastUpdate) {}

    /** A normalized, non-zero chunk cell suitable for a client heatmap. */
    public record HeatmapEntry(int chunkX, int chunkZ, float activity) {}
}
