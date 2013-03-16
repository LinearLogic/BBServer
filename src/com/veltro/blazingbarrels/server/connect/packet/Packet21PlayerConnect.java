package com.veltro.blazingbarrels.server.connect.packet;

import java.net.InetAddress;

import com.veltro.blazingbarrels.server.Configuration;
import com.veltro.blazingbarrels.server.game.Location3D;
import com.veltro.blazingbarrels.server.game.Player;

/**
 * This packet is sent by the server upon receipt of a {@link Packet20PlayerJoin}, and is used to inform all connected
 * clients of the attributes of the player that has joined (name, location, etc.) so that the clients can update their
 * game worlds.<p>
 * 
 * This packet is only ever sent by the server.
 * 
 * @author LinearLogic
 * @since 0.2.3
 */
public class Packet21PlayerConnect extends BBPacket {

	/**
	 * The username of the player connecting to the server
	 */
	private String username;

	/**
	 * The spawn location of the player connecting to the server
	 */
	private Location3D location;

	/**
	 * The health level of the player connecting to the server (usually the server's {@link Configuration#healthCap}
	 * value)
	 */
	private int health;

	/**
	 * Whether the player connecting to the server has administrator privileges
	 */
	private boolean admin;

	/**
	 * Whether the joining player is in god mode (used to determine the color of the player's energy shield)
	 */
	private boolean godMode;

	/**
	 * Whether the player connecting to the server is visible to other players (read: whether the player should be
	 * rendered in the game world and should be checked for collisions)
	 */
	private boolean vanished;

	/**
	 * {@link Player}-based constructor - calls the {@link #Packet21PlayerConnect(String, Location3D, int, boolean,
	 * boolean, boolean, InetAddress, int) complete constructor} passing all of the player's attributes.
	 * 
	 * @param player The player that has connected to the server
	 */
	public Packet21PlayerConnect(Player player) {
		this(player.getName(), player.getLocation(), player.getHealth(), player.isAdmin(), player.isGodModeEnabled(),
				player.isVanished(), player.getClientAddress(), player.getClientPort());
	}

	/**
	 * Constructs the {@link BBPacket} superclass with the ID of this packet (21), its data rendered as a string, and
	 * its Internet destination address. Initializes all class fields.
	 * 
	 * @param username The player's name
	 * @param spawnLocation The player's starting {@link #location}
	 * @param health The player's starting {@link #health}
	 * @param isAdmin The player's administrator status
	 * @param isInGodMode Whether the player is in {@link #godMode}
	 * @param isVanished Whether the player is {@link #vanished}
	 * @param address The packet's destination address
	 * @param port The port on the above address
	 */
	public Packet21PlayerConnect(String username, Location3D spawnLocation, int health, boolean isAdmin,
			boolean isInGodMode, boolean isVanished, InetAddress address, int port) {
		super(21, username + " " + (spawnLocation == null ? "." : spawnLocation.toString()) + " " + health + " " +
			(isAdmin ? "1 " : "0 ") + (isVanished ? "1" : "0"), address, port);
		this.username = username;
		location = spawnLocation;
		this.health = health;
		admin = isAdmin;
		vanished = isVanished;
	}

	/**
	 * This packet is never received by the server, so it is not handled.
	 */
	public void handle() { }

	/**
	 * @return The username of the player connecting to the server
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return The starting location of the player connecting to the server
	 */
	public Location3D getSpawnLocation() {
		return location;
	}

	/**
	 * @return The starting {@link #health} of the player connecting to the server
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * @return Whether the connecting player is an {@link #admin}
	 */
	public boolean isPlayerAdmin() {
		return admin;
	}

	/**
	 * @return Whether the connecting player is in {@link #godMode}
	 */
	public boolean isPlayerInGodMode() {
		return godMode;
	}

	/**
	 * @return Whether the connecting player is {@link #vanished}
	 */
	public boolean isPlayerVanished() {
		return vanished;
	}
}
