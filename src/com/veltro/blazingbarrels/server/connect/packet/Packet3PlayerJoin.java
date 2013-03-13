package com.veltro.blazingbarrels.server.connect.packet;

import java.net.InetAddress;

/**
 * This packet is sent from a client after receiving a positive {@link Packet1AuthResponse} and serves to inform the
 * server of the client's intent to join and play in-game. Upon receiving this packet, the server broadcasts a
 * {@link Packet4PlayerConnect} to enable clients to add the newly joined player to their game worlds. The server also
 * dispatches a {@link Packet6ServerSnapshot} to the newly connected client so it can populate its game world.
 * 
 * @author LinearLogic
 * @since 0.2.2
 */
public class Packet3PlayerJoin extends BBPacket {

	/**
	 * The username of the player joining the server
	 */
	private String username;

	/**
	 * Whether the player is connecting as a spectator (if so, the player will be flagged as invisible, in flymode, and
	 * in godmode)
	 */
	private boolean isSpectator;

	/**
	 * Constructs the {@link BBPacket} superclass with the ID of this packet (3), the data it contains, and its
	 * Internet destination address.
	 * 
	 * @param username See {@link #username}
	 * @param spectator See {@link #isSpectator}
	 * @param address The IP address from which the packet was sent (and to which a {@link Packet5ServerSnapshot}
	 * should be sent in response)
	 * @param port The port on the above address
	 */
	public Packet3PlayerJoin(String username, boolean isSpectator, InetAddress address, int port) {
		super(3, username + (isSpectator ? " s" : ""), address, port);
	}

	public void handle() {
		
	}

	/**
	 * @return The username of the connecting player
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return Whether the player is joining as a {@link #isSpectator spectator}
	 */
	public boolean isJoiningAsSpectator() {
		return isSpectator;
	}
}
