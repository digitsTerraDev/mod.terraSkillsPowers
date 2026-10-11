package com.terraskills.powers.network;

import com.terraskills.powers.TerraSkillsPowers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ToggleBrutishPayload() implements CustomPacketPayload {
    public static final ToggleBrutishPayload INSTANCE = new ToggleBrutishPayload();
    public static final Type<ToggleBrutishPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TerraSkillsPowers.MOD_ID, "toggle_brutish"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleBrutishPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
