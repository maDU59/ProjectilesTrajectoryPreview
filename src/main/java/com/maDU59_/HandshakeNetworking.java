package com.maDU59_;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class HandshakeNetworking {
    public static final Identifier HANDSHAKE_C2S = Identifier.of("mymod", "handshake_c2s");
    public static final Identifier HANDSHAKE_S2C = Identifier.of("mymod", "handshake_s2c");

    public record HANDSHAKE_C2SPayload(String message) implements CustomPayload {
        public static final CustomPayload.Id<HANDSHAKE_C2SPayload> ID = new CustomPayload.Id<>(HANDSHAKE_C2S);
        public static final PacketCodec<RegistryByteBuf, HANDSHAKE_C2SPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, HANDSHAKE_C2SPayload::message, HANDSHAKE_C2SPayload::new);
    
        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public record HANDSHAKE_S2CPayload(String message) implements CustomPayload {
        public static final CustomPayload.Id<HANDSHAKE_S2CPayload> ID = new CustomPayload.Id<>(HANDSHAKE_C2S);
        public static final PacketCodec<RegistryByteBuf, HANDSHAKE_S2CPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, HANDSHAKE_S2CPayload::message, HANDSHAKE_S2CPayload::new);
    
        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}
