package com.terraskills.powers.network;

import com.terraskills.powers.TerraSkillsPowers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** A JourneyMap client asks the server for one bounded activity window around its current map centre. */
public record ActivityHeatmapRequestPayload(int requestId, ResourceLocation dimension, int centerChunkX,
                                            int centerChunkZ, int radiusChunks) implements CustomPacketPayload {
    public static final Type<ActivityHeatmapRequestPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(TerraSkillsPowers.MOD_ID, "activity_heatmap_request"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ActivityHeatmapRequestPayload> STREAM_CODEC = StreamCodec.of(
            (buffer, payload) -> {
                buffer.writeVarInt(payload.requestId);
                buffer.writeResourceLocation(payload.dimension);
                buffer.writeVarInt(payload.centerChunkX);
                buffer.writeVarInt(payload.centerChunkZ);
                buffer.writeVarInt(payload.radiusChunks);
            },
            buffer -> new ActivityHeatmapRequestPayload(buffer.readVarInt(), buffer.readResourceLocation(),
                    buffer.readVarInt(), buffer.readVarInt(), buffer.readVarInt()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
