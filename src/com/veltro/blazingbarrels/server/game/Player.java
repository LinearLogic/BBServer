package com.veltro.blazingbarrels.server.game;

import java.util.HashSet;
import java.util.Set;

import com.veltro.blazingbarrels.server.BBServer;
import com.veltro.blazingbarrels.server.Configuration;

/**
 * Represents an in-game player connected to the server
 * 
 * @author LinearLogic
 * @since 0.1.1
 */
public class Player {

	/**
	 * The name the player chose when connecting to the server
	 */
	private final String name;

	/**
	 * The player's {@link Location3D location} within the game world
	 */
	private Location3D location;

	/**
	 * Flag for administrator status - if 'true', the player is an admin and has a number of privileges, such as an
	 * immunity to being kicked.
	 */
	private boolean isAdmin;

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
	 * Constructor
	 * 
	 * @param name The player's unique username
	 * @param location The player's spawn point
	 */
	public Player(String name, Location3D location) {
		this.name = name;
		setLocation(location);
		setHealth(BBServer.getConfig().getHealthCap());
		setAdmin(false);
		setVanished(false);
		flyMode = false; // These values are initialized directly as they don't
		godMode = false; // have ChangeTypes associated with them
	}

	/**
	 * @return The recent {@link #changes} to the player
	 */
	public ChangeType[] getChanges() {
		return (ChangeType[]) changes.toArray();
	}

	/**
	 * Empties the list of recent {@link #changes} made to the player
	 */
	public void clearChanges() {
		changes.clear();
	}

	/**
	 * Kicks the player from the server
	 */
	public void kick() {
		changes.add(ChangeType.DISCONNECT_KICK);
	}

	/**
	 * @return The player's name
	 */
	public String getName() {
		return name;
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
		return isAdmin;
	}

	/**
	 * Sets whether the player {@link #isAdmin}
	 * 
	 * @param status 'true' add the administrator status to the player, 'false' to revoke it
	 */
	public void setAdmin(boolean status) {
		if (isAdmin == status)
			return;
		isAdmin = status;
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
