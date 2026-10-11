package com.terraskills.powers.mining;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;

/** Per-dimension persistent record of blocks placed or replaced by players. */
public final class CarefulEyeProvenance extends SavedData {
    private static final String DATA_ID = "terraskillspowers_careful_eye_provenance";
    private final Set<Long> placedPositions = new HashSet<>();

    public static CarefulEyeProvenance get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(CarefulEyeProvenance::new, CarefulEyeProvenance::load, DataFixTypes.LEVEL), DATA_ID);
    }

    private static CarefulEyeProvenance load(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        CarefulEyeProvenance data = new CarefulEyeProvenance();
        ListTag positions = tag.getList("placed", net.minecraft.nbt.Tag.TAG_LONG);
        for (int index = 0; index < positions.size(); index++) {
            if (positions.get(index) instanceof net.minecraft.nbt.LongTag position) data.placedPositions.add(position.getAsLong());
        }
        return data;
    }

    public void markPlaced(BlockPos pos) {
        if (placedPositions.add(pos.asLong())) setDirty();
    }

    public boolean consumePlaced(BlockPos pos) {
        boolean placed = placedPositions.remove(pos.asLong());
        if (placed) setDirty();
        return placed;
    }

    @Override
    public CompoundTag save(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        ListTag positions = new ListTag();
        for (long pos : placedPositions) positions.add(net.minecraft.nbt.LongTag.valueOf(pos));
        tag.put("placed", positions);
        return tag;
    }
}
