package com.veltro.blazingbarrels.server.connect.packet;

import java.net.InetAddress;

/**
 * This packet is sent by a client upon voluntarily disconnecting from a server, or from a server to notify clients
 * that a player has disconnected. In the latter case, this packet includes the reason for the disconnect.<p>
 * 
 * This packet is both sent and received by the server.
 * 
 * @author LinearLogic
 * @since 0.2.4
 */
public class Packet22PlayerDisconnect extends BBPacket {

	/**
	 * The name of the player who is disconnecting (or being disconnected) from the server 
	 */
	private String username;

	/**
	 * The reason for the player's being disconnected from the server (used to specify whether the playe was kicked,
	 * timed out, etc.). This is only used when the packet is sent from a server to a client.
	 */
	private String reason;

	/**
	 * Constructs the {@link BBPacket} superclass with the ID of this packet (22), its data rendered as a string, and
	 * its Internet destination address. Initializes all class fields.
	 * 
	 * @param username The username of the player disconnecting from the server
	 * @param reason The reason for the player's disconnection
	 * @param address The IP address from which the packet was sent
	 * @param port The port on the above address
	 */
	public Packet22PlayerDisconnect(String username, String reason, InetAddress address, int port) {
		super(22, username + (reason == null || reason.equals("") ? "" : " " + reason), address, port);
		this.username = username;
		this.reason = reason;
	}

	public void handle() {
		
	}

	/**
	 * @return The name of the player disconnecting from the server
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return The {@link #reason} for the disconnection
	 */
	public String getReason() {
		return reason;
	}
}
