package com.maDU59_;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maDU59_.HandshakeNetworking.HANDSHAKE_C2SPayload;
import com.maDU59_.HandshakeNetworking.HANDSHAKE_S2CPayload;

public class Ptp implements ModInitializer {
	public static final String MOD_ID = "ptp";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		PayloadTypeRegistry.playC2S().register(HANDSHAKE_C2SPayload.ID, HANDSHAKE_C2SPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(HANDSHAKE_S2CPayload.ID, HANDSHAKE_S2CPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(HANDSHAKE_C2SPayload.ID,
            (payload, context) -> {
                // Send back a reply packet
                ServerPlayNetworking.send(context.player(), new HANDSHAKE_S2CPayload("Is installed on server"));
				LOGGER.info("[PTP] Sending handshake to player...");
            });

		LOGGER.info("Hello Fabric world!");
	}
}