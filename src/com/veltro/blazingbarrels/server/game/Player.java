package com.veltro.blazingbarrels.server.game;

import com.veltro.blazingbarrels.server.BBServer;

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

	public Player(String name, Location3D location) {
		this.name = name;
		this.location = location;
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
		if (health < 1) {
			// TODO: Respawn player
			return;
		}
		if (health > BBServer.getConfig().getHealthCap()) {
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
		health += amount;
		if (health > BBServer.getConfig().getHealthCap()) {
			health = BBServer.getConfig().getHealthCap();
			return;
		}
		if (health < 1) {
			// TODO: respawn the player
			return;
		}
	}

	/**
	 * Decreases the player's {@link #health} by the provided amount
	 * 
	 * @param amount An integer value, normally positive
	 */
	public void damage(int amount) {
		health -= amount;
		if (health > BBServer.getConfig().getHealthCap()) {
			health = BBServer.getConfig().getHealthCap();
			return;
		}
		if (health < 1) {
			// TODO: respawn the player
			return;
		}
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
	 * @param active 'true' to enabled fly mode, 'false' to disable it
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
		this.vanished = vanished;
	}
}
