package com.veltro.blazingbarrels.server.connect.packet;

import java.net.InetAddress;

/**
 * This packet is sent to a client from a BBServer instance in response to the client's sending a
 * {@link Packet00AuthRequest} to attempt authorization in order to join the server. This packet contains the server's
 * verdict, and if the user was successfully authorized, the server will now wait for the client to send a
 * {@link Packet20PlayerJoin} to begin interaction with the server.<p>
 * 
 * This packet is only ever sent by the server.
 * 
 * @author LinearLogic
 * @since 0.0.6
 */
public class Packet01AuthResponse extends BBPacket {

	/**
	 * The username of the account that attempted authorization on a BBServer instance and is now receiving its verdict
	 */
	private String username;

	/**
	 * The user's authorization verdict - '0' if not authorized due to the server's player cap being reached, '1' if
	 * not authorized due to the provided {@link #username} being taken, '2' if not authorized due to an incorrect
	 * password, '3' if successfully authorized.
	 */
	private int authorized;

	/**
	 * Constructs the {@link BBPacket} superclass with the ID of this packet (1), its data rendered as a string, and
	 * its Internet destination address. Initializes all class fields.
	 * 
	 * @param username An account's {@link #username}
	 * @param authorizationVerdict 'true' if the player was successfully authorized (ie. if the password was accepted),
	 * else 'false'
	 * @param address The IP address from which the packet was sent
	 * @param port The port on the above address
	 */
	public Packet01AuthResponse(String username, int authorizationVerdict, InetAddress address, int port) {
		super(1, "username " + authorizationVerdict, address, port);
		this.username = username;
		authorized = authorizationVerdict;
	}

	/**
	 * This packet is never received by the server, so it is not handled.
	 */
	public void handle() { }

	/**
	 * @return The {@link #username} associated with the authorizations      verdict received from the server
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return The {@link #authorized ID} representing the authorization verdict
	 */
	public int getAuthorizationVerdictID() {
		return authorized;
	}
}
