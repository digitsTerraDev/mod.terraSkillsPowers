package com.terraskills.powers.network;

import com.terraskills.powers.TerraSkillsPowers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Exact server-side handcart values for the puller HUD. */
public record HandcartWeightPayload(boolean attached, double cartWeightKg, double normalLimitKg, double hardLimitKg)
        implements CustomPacketPayload {
    public static final Type<HandcartWeightPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(TerraSkillsPowers.MOD_ID, "handcart_weight"));
    public static final StreamCodec<RegistryFriendlyByteBuf, HandcartWeightPayload> STREAM_CODEC = StreamCodec.of(
            (buffer, payload) -> {
                buffer.writeBoolean(payload.attached);
                buffer.writeDouble(payload.cartWeightKg);
                buffer.writeDouble(payload.normalLimitKg);
                buffer.writeDouble(payload.hardLimitKg);
            },
            buffer -> new HandcartWeightPayload(buffer.readBoolean(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
