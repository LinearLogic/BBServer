package com.veltro.blazingbarrels.server.connect.packet;

import java.net.InetAddress;

/**
 * This packet is sent to a client from a BBServer instance in response to the client's sending a
 * {@link Packet0AuthRequest} to attempt authorization in order to join the server. This packet contains the server's
 * verdict, and if the user was successfully authorized, the server will now wait for the client to send a
 * {@link Packet2PlayerJoin} to begin interaction with the server.<p>
 * This packet is only ever sent (never received) by the server.
 * 
 * @author LinearLogic
 * @since 0.0.6
 */
public class Packet1AuthResponse extends BBPacket {

	/**
	 * The username of the account that attempted authorization on a BBServer instance and is now receiving its verdict
	 */
	private String username;

	/**
	 * The user's authorization verdict - 'true' if authorized, else false
	 */
	private boolean authorized;

	/**
	 * Constructs the {@link BBPacket} superclass with the ID of this packet (1), the data it contains, and its
	 * Internet source location.
	 * 
	 * @param username An account's {@link #username}
	 * @param authorizationVerdict A String flag that is either "1" or "0" depending on the result of the user's
	 * attempt at authorization on the server
	 * @param address The location from which the packet was sent
	 * @param port The port on the address from which the packet was sent
	 */
	public Packet1AuthResponse(String username, String authorizationVerdict, InetAddress address, int port) {
		super(1, "username " + authorizationVerdict, address, port);
		this.username = username;
		authorized = (authorizationVerdict.equals("1")) ? true : false;
	}

	/**
	 * @return The {@link #username} associated with the authorizations      verdict received from the server
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return Whether or not the user was successfully authorized to play on the server he/she tried to connect to
	 */
	public boolean isAuthorized() {
		return authorized;
	}
}
