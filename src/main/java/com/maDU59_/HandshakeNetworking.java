package com.maDU59_;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class HandshakeNetworking {
    public static final ResourceLocation HANDSHAKE_C2S = ResourceLocation.fromNamespaceAndPath("mymod", "handshake_c2s");
    public static final ResourceLocation HANDSHAKE_S2C = ResourceLocation.fromNamespaceAndPath("mymod", "handshake_s2c");

    public record HANDSHAKE_C2SPayload(String message) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<HANDSHAKE_C2SPayload> ID = new CustomPacketPayload.Type<>(HANDSHAKE_C2S);
        public static final StreamCodec<RegistryFriendlyByteBuf, HANDSHAKE_C2SPayload> CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, HANDSHAKE_C2SPayload::message, HANDSHAKE_C2SPayload::new);
    
        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return ID;
        }
    }

    public record HANDSHAKE_S2CPayload(String message) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<HANDSHAKE_S2CPayload> ID = new CustomPacketPayload.Type<>(HANDSHAKE_S2C);
        public static final StreamCodec<RegistryFriendlyByteBuf, HANDSHAKE_S2CPayload> CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, HANDSHAKE_S2CPayload::message, HANDSHAKE_S2CPayload::new);
    
        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return ID;
        }
    }
}
