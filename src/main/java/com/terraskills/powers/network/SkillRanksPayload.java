package com.terraskills.powers.network;

import com.terraskills.powers.TerraSkillsPowers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SkillRanksPayload(int mining, int deepMining, int stoneMining, int excavating) implements CustomPacketPayload {
    public static final Type<SkillRanksPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TerraSkillsPowers.MOD_ID, "skill_ranks"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SkillRanksPayload> STREAM_CODEC = StreamCodec.of(
            (buffer, payload) -> { buffer.writeVarInt(payload.mining); buffer.writeVarInt(payload.deepMining); buffer.writeVarInt(payload.stoneMining); buffer.writeVarInt(payload.excavating); },
            buffer -> new SkillRanksPayload(buffer.readVarInt(), buffer.readVarInt(), buffer.readVarInt(), buffer.readVarInt()));

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
