package com.veltro.blazingbarrels.server.connect;

import com.veltro.blazingbarrels.server.BBServer;
import com.veltro.blazingbarrels.server.connect.packet.Packet02DeauthWarning;
import com.veltro.blazingbarrels.server.game.Player;
import com.veltro.blazingbarrels.server.game.World;

/**
 * A task thread used to send {@link Packet02DeauthWarning} packets to a client that has been authorized to join but
 * has not responded with a {@link Packet20PlayerJoin}. If the maximum number of {@link #warnings} are sent, with a
 * specified {@link #timeout} in between each, the player is disconnected from the server.
 * 
 * @author LinearLogic
 * @since 0.3.0
 */
public class DeauthTask extends Thread {

	/**
	 * The player whose client is the recipient of the {@link Packet02DeauthWarning} packets being sent, and who will
	 * be removed from the server if a response is not received within the allotted time.
	 */
	private Player player;

	/**
	 * The number of {@link Packet02DeauthWarning} packets to send to the client before disconnecting it
	 */
	private int warnings;

	/**
	 * The amount of time (in milliseconds) to wait in between {@link Packet02DeauthWarning} dispatches
	 */
	private int timeout;

	/**
	 * Constructs the Thread superclass and initializes the {@link #player}, {@link #warnings}, and {@link #timeout}
	 * fields to the provided values.
	 * 
	 * @param player
	 * @param warnings
	 * @param timeout
	 */
	public DeauthTask(Player player, int warnings, int timeout) {
		super("DeauthTask");
		this.player = player;
		this.warnings = warnings;
		this.timeout = timeout;
	}

	/**
	 * Pauses for the length of the {@link #timeout} field and then sends a {@link Packet02DeauthWarning}. This process
	 * is repeated as many times as is specified in the {@link #warnings} field, and then the player is disconnected
	 * from the server for timing out.
	 */
	public void run() {
		Packet02DeauthWarning packet = new Packet02DeauthWarning(player.getName(), player.getClientAddress(),
				player.getClientPort());
		for (int i = 0; i == warnings; i++) {
			try {
				wait(timeout);
			} catch (InterruptedException e) { // The client has sent a PlayerJoin packet; cease the deauth warnings
				System.err.println("Stopping the deauth task for player " + player.getName() + "!"); // for debugging
				return;
			}
			if (i < warnings)
				BBServer.getSenderDaemon().outgoingPacketQueue.add(packet);
			else
				World.getPlayer(player.getName()).disconnect(1); // Disconnect the player, citing a network connection timeout
		}
	}
}
