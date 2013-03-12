package com.veltro.blazingbarrels.server.connect.packet;

import java.net.InetAddress;

/**
 * The packet sent by the client when attempting to authorize on a server in order to join and play.<p>
 * This packet is only ever received (never sent) by the server, which responds with a {@link Packet1AuthResponse}.
 * 
 * @author LinearLogic
 * @since 0.0.5
 */
public class Packet0AuthRequest extends BBPacket {

	/**
	 * The username of the player attempting to log on to a BBServer instance to play
	 */
	private String username;

	/**
	 * The password submitted by the player attempting authentication. If this password matches that of the
	 * server being connected to, then the player is allowed to join and play.
	 */
	private String password;

	/**
	 * Constructs the {@link BBPacket} superclass with the ID of this packet (0), the data it contains, and its
	 * Internet destination location.
	 * 
	 * @param username An account's {@link #username}
	 * @param password The {@link #password}, which in the event of successful authentication will match the password
	 * of the server
	 * @param address The location from which the packet was sent (and to which a response packet should be sent)
	 * @param port The port on the address from which the packet was sent
	 */
	public Packet0AuthRequest(String username, String password, InetAddress address, int port) {
		super(0, username + (password.equals("") || password == null ? "" : " " + password), address, port);
		this.username = username;
		this.password = password;
	}

	/**
	 * @return The {@link #username} of the player attempting authentication with this packet
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return The {@link #password} given by the player attempting authentication with this packet
	 */
	public String getPassword() {
		return password;
	}
}
