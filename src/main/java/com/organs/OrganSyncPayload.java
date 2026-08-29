package com.organs;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * Server → client push of the player's own organ condition, so the HUD has something to draw.
 */
public record OrganSyncPayload(OrganData data) implements CustomPayload {
    public static final CustomPayload.Id<OrganSyncPayload> ID =
            new CustomPayload.Id<>(Identifier.of(OrgansMod.MOD_ID, "sync"));

    public static final PacketCodec<RegistryByteBuf, OrganSyncPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, p -> p.data().heart(),
            PacketCodecs.VAR_INT, p -> p.data().lungs(),
            PacketCodecs.VAR_INT, p -> p.data().liver(),
            PacketCodecs.VAR_INT, p -> p.data().brain(),
            (heart, lungs, liver, brain) -> new OrganSyncPayload(new OrganData(heart, lungs, liver, brain))
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
