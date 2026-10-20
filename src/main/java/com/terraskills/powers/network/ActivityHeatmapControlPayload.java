package com.terraskills.powers.network;

import com.terraskills.powers.TerraSkillsPowers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Explicit server-side permission for the local JourneyMap activity debug overlay. */
public record ActivityHeatmapControlPayload(boolean enabled) implements CustomPacketPayload {
    public static final Type<ActivityHeatmapControlPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(TerraSkillsPowers.MOD_ID, "activity_heatmap_control"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ActivityHeatmapControlPayload> STREAM_CODEC = StreamCodec.of(
            (buffer, payload) -> buffer.writeBoolean(payload.enabled),
            buffer -> new ActivityHeatmapControlPayload(buffer.readBoolean()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
