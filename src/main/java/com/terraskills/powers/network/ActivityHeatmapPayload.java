package com.terraskills.powers.network;

import com.terraskills.powers.TerraSkillsPowers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/** Server-authoritative normalized shared-activity cells for the JourneyMap renderer. */
public record ActivityHeatmapPayload(int requestId, ResourceLocation dimension, int minChunkX, int minChunkZ,
                                     int maxChunkX, int maxChunkZ, List<ChunkActivity> entries)
        implements CustomPacketPayload {
    private static final int MAX_WIRE_ENTRIES = 4096;
    public static final Type<ActivityHeatmapPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(TerraSkillsPowers.MOD_ID, "activity_heatmap"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ActivityHeatmapPayload> STREAM_CODEC = StreamCodec.of(
            ActivityHeatmapPayload::encode, ActivityHeatmapPayload::decode);

    public ActivityHeatmapPayload {
        entries = List.copyOf(entries);
    }

    private static void encode(RegistryFriendlyByteBuf buffer, ActivityHeatmapPayload payload) {
        if (payload.entries.size() > MAX_WIRE_ENTRIES) {
            throw new IllegalArgumentException("Too many activity heatmap entries: " + payload.entries.size());
        }
        buffer.writeVarInt(payload.requestId);
        buffer.writeResourceLocation(payload.dimension);
        buffer.writeVarInt(payload.minChunkX);
        buffer.writeVarInt(payload.minChunkZ);
        buffer.writeVarInt(payload.maxChunkX);
        buffer.writeVarInt(payload.maxChunkZ);
        buffer.writeVarInt(payload.entries.size());
        for (ChunkActivity entry : payload.entries) {
            buffer.writeVarInt(entry.chunkX);
            buffer.writeVarInt(entry.chunkZ);
            buffer.writeFloat(entry.activity);
        }
    }

    private static ActivityHeatmapPayload decode(RegistryFriendlyByteBuf buffer) {
        int requestId = buffer.readVarInt();
        ResourceLocation dimension = buffer.readResourceLocation();
        int minChunkX = buffer.readVarInt();
        int minChunkZ = buffer.readVarInt();
        int maxChunkX = buffer.readVarInt();
        int maxChunkZ = buffer.readVarInt();
        int size = buffer.readVarInt();
        if (size < 0 || size > MAX_WIRE_ENTRIES) {
            throw new IllegalArgumentException("Invalid activity heatmap entry count: " + size);
        }
        List<ChunkActivity> entries = new ArrayList<>(size);
        for (int index = 0; index < size; index++) {
            entries.add(new ChunkActivity(buffer.readVarInt(), buffer.readVarInt(), buffer.readFloat()));
        }
        return new ActivityHeatmapPayload(requestId, dimension, minChunkX, minChunkZ, maxChunkX, maxChunkZ, entries);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public record ChunkActivity(int chunkX, int chunkZ, float activity) {}
}
