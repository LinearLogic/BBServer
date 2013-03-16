package com.veltro.blazingbarrels.server.game;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

import com.veltro.blazingbarrels.server.BBServer;
import com.veltro.blazingbarrels.server.Configuration;
import com.veltro.blazingbarrels.server.connect.packet.Packet10ServerSnapshot;

/**
 * Represents an in-game player connected to the server
 * 
 * @author LinearLogic
 * @since 0.1.1
 */
public class Player {

	/**
	 * The radius, in pixels, of the "energy shield" sphere rendered around each player (used to handle collisions)
	 */
	public static final int SHIELD_RADIUS = 75;

	/**
	 * The name the player chose when connecting to the server, used to identify it in packets sent between the server
	 * and any connected Blazing Barrels clients
	 */
	private final String name;

	/**
	 * The IP address of the Blazing Barrels client to which this player belongs (may change over time)
	 */
	private InetAddress clientAddress;

	/**
	 * The port on the {@link #clientAddress} (may change over time)
	 */
	private int clientPort;

	/**
	 * The player's {@link Location3D location} within the game world
	 */
	private Location3D location;

	/**
	 * Flag for administrator status - if 'true', the player is an admin and has a number of privileges, such as an
	 * immunity to being kicked.
	 */
	private boolean admin;

	/**
	 * Status flag for flight - if 'true', gravity does not affect the player
	 */
	private boolean flyMode;

	/**
	 * Status flag for invincibility - if 'true', the player cannot be damaged
	 */
	private boolean godMode;

	/**
	 * Status flag for visibility - if 'true', the player cannot be seen by other players, and its location is not
	 * sent to connected clients.
	 */
	private boolean vanished;

	/**
	 * The player's current health level, an integer between 1 and the health cap specified in the
	 * {@link Configuration}, inclusive)
	 */
	private int health;

	/**
	 * A list of the types of updates to the player since the last time its information was sent to connected clients
	 */
	private Set<ChangeType> changes = new HashSet<ChangeType>();

	/**
	 * Simplest constructor. Calls the {@link #Player(String, InetAddress, int, Location3D, int, boolean, boolean,
	 * boolean, boolean) complete constructor} with the provided name, address, and port, and default values for every
	 * other field.
	 * 
	 * @param name The unique username of the player
	 * @param clientAddress The player's {@link #clientAddress IP address}
	 * @param clientPort The port on the above address
	 */
	public Player(String name, InetAddress clientAddress, int clientPort) {
		this(name, clientAddress, clientPort, World.getRandomSpawnPoint(), BBServer.getConfig().getHealthCap(), false,
				false, false, false);
	}

	/**
	 * Simplified constructor. Calls the {@link #Player(String, InetAddress, int, Location3D, int, boolean, boolean,
	 * boolean, boolean) complete constructor} with the provided name, address, port, and location, and default values
	 * for every other field.
	 * 
	 * @param name The unique username of the player
	 * @param clientAddress The player's {@link #clientAddress IP address}
	 * @param clientPort The port on the above address
	 * @param location The player's spawn point
	 */
	public Player(String name, InetAddress clientAddress, int clientPort, Location3D location) {
		this(name, clientAddress, clientPort, location, BBServer.getConfig().getHealthCap(), false, false, false,
				false);
	}

	/**
	 * Complete constructor
	 * 
	 * @param name The unique username of the player
	 * @param clientAddress The player's {@link #clientAddress IP address}
	 * @param clientPort The port on the above address
	 * @param location The player's starting {@link #location}
	 * @param health The player's starting health level
	 * @param isAdmin Whether the player has administrator privileges
	 * @param inFlyMode Whether the player can fly
	 * @param inGodMode Whether the player is invincible
	 * @param isVanished Whether the player is visible to other players
	 */
	public Player(String name, InetAddress clientAddress, int clientPort, Location3D location, int health,
			boolean isAdmin, boolean inFlyMode, boolean inGodMode, boolean isVanished) {
		this.name = name;
		this.clientAddress = clientAddress;
		this.clientPort = clientPort;
		this.location = location;
		this.health = health;
		admin = isAdmin;
		flyMode = inFlyMode;
		godMode = inGodMode;
		vanished = isVanished;
	}

	/**
	 * @return A string containing all of the player's data needed to add the player to a {@link Packet10ServerSnapshot}
	 */
	public String generateSnapshotString() {
		return name + "." + location.toString() + "." + health + "." + (admin ? "1." : "0.") + (vanished ? "1" : "0");
	}

	/**
	 * @return The recent {@link #changes} to the player
	 */
	public ChangeType[] getChanges() {
		ChangeType[] output = new ChangeType[changes.size()];
		int index = 0;
		for (ChangeType type : changes) {
			switch(type) {
				case DISCONNECT_KICK:
				case DISCONNECT_TIMEOUT:
				case DISCONNECT_QUIT:
					return new ChangeType[] {type};
				default:
					output[index++] = type;
					break;
			}
		}
		return output;
	}

	/**
	 * Empties the list of recent {@link #changes} made to the player
	 */
	public void clearChanges() {
		changes.clear();
	}

	/**
	 * Disconnects the player from the server with the provided reason ID
	 * 
	 * @param reasonID 0 if the player quit voluntarily (the server should never be deciding this), 1 if the player's network
	 * connection timed out, and 2 if the player was kicked
	 */
	public void disconnect(int reasonID) {
		switch(reasonID) {
			case 0:
				changes.clear();
				changes.add(ChangeType.DISCONNECT_QUIT);
				break;
			case 1:
				changes.clear();
				changes.add(ChangeType.DISCONNECT_TIMEOUT);
				break;
			case 2:
				changes.clear();
				changes.add(ChangeType.DISCONNECT_KICK);
			default:
				// TODO: log that the server attempted to disconnect a player for an invalid reason
				break;
		}
	}

	/**
	 * @return The player's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The player's {@link #clientAddress}
	 */
	public InetAddress getClientAddress() {
		return clientAddress;
	}

	/**
	 * @return The port on the player's {@link #clientAddress}
	 */
	public int getClientPort() {
		return clientPort;
	}

	/**
	 * @return The player's {@link #location}
	 */
	public Location3D getLocation() {
		return location;
	}

	/**
	 * Sets the player's {@link #location} to the specified three dimensional location
	 * 
	 * @param location A {@link Location3D} object
	 */
	public void setLocation(Location3D location) {
		this.location = location;
		changes.add(ChangeType.LOCATION);
	}

	/**
	 * Moves the player to the specified coordinate position, leaving the player's rotation unchanged.
	 * 
	 * @param x The x-coordinate, in pixels, of the player's destination
	 * @param y The y-coordinate, in pixels, of the player's destination
	 * @param z The z-coordinate, in pixels, of the player's destination
	 */
	public void teleport(float x, float y, float z) {
		location.setPosition(x, y, z);
		changes.add(ChangeType.LOCATION);
	}

	/**
	 * @return The player's health level
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * Sets the player's {@link #health} to the specified integer, handling out-of-bounds values
	 * 
	 * @param health The player's new health level
	 */
	public void setHealth(int health) {
		if (health == this.health)
			return;
		changes.add(ChangeType.HEALTH);
		if (health < 1) {
			health = BBServer.getConfig().getHealthCap();
			setLocation(World.getRandomSpawnPoint());
			// Broadcast that the player has been killed and has respawned
			return;
		}
		if (health > BBServer.getConfig().getHealthCap()) {
			if (this.health == BBServer.getConfig().getHealthCap()) {
				changes.remove(ChangeType.HEALTH);
				return;
			}
			health = BBServer.getConfig().getHealthCap();
			return;
		}
		this.health = health;
	}

	/**
	 * Increases the player's {@link #health} by the provided amount
	 * 
	 * @param amount An integer value, normally positive
	 */
	public void heal(int amount) {
		setHealth(health + amount);
	}

	/**
	 * Decreases the player's {@link #health} by the provided amount
	 * 
	 * @param amount An integer value, normally positive
	 */
	public void damage(int amount) {
		setHealth(health - amount);
	}

	/**
	 * @return Whether the player is an administrator
	 */
	public boolean isAdmin() {
		return admin;
	}

	/**
	 * Sets whether the player is an {@link #admin}
	 * 
	 * @param status 'true' add the administrator status to the player, 'false' to revoke it
	 */
	public void setAdmin(boolean status) {
		if (admin == status)
			return;
		admin = status;
		changes.add(ChangeType.ADMIN);
	}

	/**
	 * @return Whether the player is in {@link #flyMode}
	 */
	public boolean isFlyModeEnabled() {
		return flyMode;
	}

	/**
	 * Sets whether the player is in {@link #flyMode}
	 * 
	 * @param active 'true' to enable fly mode, 'false' to disable it
	 */
	public void setFlyMode(boolean active) {
		flyMode = active;
	}

	/**
	 * @return Whether the player is in {@link #godMode}
	 */
	public boolean isGodModeEnabled() {
		return godMode;
	}

	/**
	 * Sets whether the player is in {@link #godMode}
	 * 
	 * @param active 'true' to enable god mode, 'false' to disable it
	 */
	public void setGodMode(boolean active) {
		godMode = active;
	}

	/**
	 * @return Whether the player is {@link #vanished}
	 */
	public boolean isVanished() {
		return vanished;
	}

	/**
	 * Sets whether the player is {@link #vanished}
	 * 
	 * @param vanished 'true' to hide the player, 'false' to reveal the player
	 */
	public void setVanished(boolean vanished) {
		if (this.vanished == vanished)
			return;
		this.vanished = vanished;
		changes.add(ChangeType.VISIBILITY);
	}
}
