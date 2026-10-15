package com.terraskills.powers.network;

import com.terraskills.powers.TerraSkillsPowers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/** Server-authoritative horse-cart load and capacity for the horse HUD. */
public record HorseWeightPayload(boolean riding, UUID horseId, double loadWeightKg, double capacityKg,
                                 double backWeightKg, double backCapacityKg, boolean jumpBlocked,
                                 boolean noStepAssist, boolean movementBlocked) implements CustomPacketPayload {
    public static final Type<HorseWeightPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(TerraSkillsPowers.MOD_ID, "horse_weight"));
    public static final StreamCodec<RegistryFriendlyByteBuf, HorseWeightPayload> STREAM_CODEC = StreamCodec.of(
            (buffer, payload) -> {
                buffer.writeBoolean(payload.riding);
                buffer.writeUUID(payload.horseId);
                buffer.writeDouble(payload.loadWeightKg);
                buffer.writeDouble(payload.capacityKg);
                buffer.writeDouble(payload.backWeightKg);
                buffer.writeDouble(payload.backCapacityKg);
                buffer.writeBoolean(payload.jumpBlocked);
                buffer.writeBoolean(payload.noStepAssist);
                buffer.writeBoolean(payload.movementBlocked);
            },
            buffer -> new HorseWeightPayload(buffer.readBoolean(), buffer.readUUID(), buffer.readDouble(), buffer.readDouble(),
                    buffer.readDouble(), buffer.readDouble(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
