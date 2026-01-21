package fr.madu59.ptp;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import fr.madu59.ptp.HandshakeNetworking.HANDSHAKE_C2SPayload;
import fr.madu59.ptp.HandshakeNetworking.HANDSHAKE_S2CPayload;

@Mod(Ptp.MOD_ID)
public class Ptp{
	public static final String MOD_ID = "ptp";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LogUtils.getLogger();

	public Ptp(IEventBus modEventBus, ModContainer modContainer) {

		modEventBus.addListener(this::registerNetworking);
	}

	private void registerNetworking(final RegisterPayloadHandlersEvent event) {

        final PayloadRegistrar registrar = event.registrar("ptp"); 

		// Register C2S (Client to Server) Handshake
		registrar.playToServer(
			HANDSHAKE_C2SPayload.ID,
			HANDSHAKE_C2SPayload.CODEC,
			(payload, context) -> {
				// context.player() is the player who sent the packet
				LOGGER.info("[PTP] Sending handshake to player...");
				
				// Sending back the S2C reply
				context.reply(new HANDSHAKE_S2CPayload("Is installed on server"));
			}
		);

		// Register S2C (Server to Client) so the client knows how to decode the reply
		registrar.playToClient(
			HANDSHAKE_S2CPayload.ID,
			HANDSHAKE_S2CPayload.CODEC,
			(payload, context) -> {
				LOGGER.info("[PTP] Received handashake from server!");
				PtpClient.serverHasMod = true;
			}
		);
    }
}