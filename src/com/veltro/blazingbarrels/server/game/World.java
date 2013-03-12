package com.veltro.blazingbarrels.server.game;

import java.util.ArrayList;

/**
 * This static class represents the game world and contains a number of its attributes as fields, such as the
 * {@link #RADIUS} and {@link #players list of players}.
 * 
 * @author LinearLogic
 * @since 0.1.2
 */
public class World {

	/**
	 * The radius, in pixels, of the world's border
	 */
	public final int RADIUS = 500;

	/**
	 * A list of the {@link Player players} currently connected to the server
	 */
	private ArrayList<Player> players = new ArrayList<Player>();

	/**
	 * An Array of the locations used as spawn points. When a player spawns, one is chosen at random.
	 */
	private Location3D[] spawnPoints = {new Location3D(0, 0, -450, 0, 0, 0), new Location3D(0, 0, 450, 180, 0, 0),
			new Location3D(450, 0, 0, 90, 0, 0), new Location3D(-450, 0, 0, 270, 0, 0)};

	/**
	 * Adds the provided {@link Player} object to the list of {@link #players} on the server
	 * 
	 * @param player
	 */
	public void addPlayer(Player player) {
		players.add(player);
	}

	/**
	 * @return The currently connected players as an Array
	 */
	public Player[] getPlayers() {
		Player[] output = new Player[players.size()];
		for (int i = 0; i < players.size(); i++)
			output[i] = players.get(i);
		return output;
	}

	/**
	 * @return One of the locations in the {@link #spawnPoints} Array, selected at random
	 */
	public Location3D getRandomSpawnPoint() {
		return spawnPoints[(int) (Math.random() * spawnPoints.length)];
	}
}
