package com.veltro.blazingbarrels.server.connect.packet;

import java.net.InetAddress;

/**
 * This packet is sent by a BBServer instance to the client if the client's current user account has been successfully
 * authorized but the client has not sent a {@link Packet3PlayerJoin} to begin playing on the server and a certain
 * amount of time has passed. If no response from the client has been received after sending three of these packets,
 * the user's account will be deauthorized on the server, meaning that a new login handshake will have to occurr before
 * the client can rejoin the server.<p>
 * This packet is only ever sent (never received) by the server.
 * 
 * @author LinearLogic
 * @since 0.0.7
 */
public class Packet2DeauthWarning extends BBPacket {

	/**
	 * The username of the account that will be deauthorized if no response to this packet is received by the server
	 */
	private String username;

	/**
	 * Constructs the {@link BBPacket} superclass with the ID of this packet (2), the {@link #username} associated with
	 * the deauth warning, and the packet's Internet source location.
	 * @param username
	 * @param address
	 * @param port
	 */
	public Packet2DeauthWarning(String username, InetAddress address, int port) {
		super(2, username, address, port);
		this.username = username;
	}

	/**
	 * @return The {@link #username} associated with this deauthorization warning
	 */
	public String getUsername() {
		return username;
	}
}
