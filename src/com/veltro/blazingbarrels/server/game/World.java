package com.veltro.blazingbarrels.server.game;

import java.util.ArrayList;

/**
 * This static class represents the game world and contains a number of its attributes as fields, such as the
 * {@link #players list of players} in-game.
 * 
 * @author LinearLogic
 * @since 0.1.2
 */
public class World {

	/**
	 * The maximum value of the world radius that can be set in the {@link Configuration}
	 */
	public static final int MAX_RADIUS = 10000;

	/**
	 * A list of the {@link Player players} currently connected to the server
	 */
	private static ArrayList<Player> players = new ArrayList<Player>();

	/**
	 * A list of the locations used as spawn points. When a player spawns, one is chosen at random.
	 */
	private static ArrayList<Location3D> spawnPoints = new ArrayList<Location3D>();

	/**
	 * Adds the provided {@link Player} object to the list of {@link #players} on the server
	 * 
	 * @param player
	 */
	public static void addPlayer(Player player) {
		players.add(player);
	}

	/**
	 * Removes the provided {@link Player} object from the {@link #players list of players} on the server, provided the
	 * player is present in the list.
	 * 
	 * @param player
	 * @return 'true' if the list contained the provided player and that player was removed, else 'false'
	 */
	public static boolean removePlayer(Player player) {
		return players.remove(player);
	}

	/**
	 * Searches the {@link #players list of players} for one whose name matches the provided string. If a match is
	 * found, the player is removed from the player list.
	 * 
	 * @param name
	 * @return 'true' if a player with the provided name is found and removed, else 'false'
	 */
	public static boolean removePlayer(String name) {
		for (Player p : players)
			if (p.getName().equalsIgnoreCase(name)) {
				players.remove(p);
				return true;
			}
		return false;
	}

	/**
	 * Empties the {@link #players list of players}
	 */
	public static void clearPlayers() {
		players.clear();
	}

	/**
	 * @return The currently connected players as an Array
	 */
	public static Player[] getPlayers() {
		Player[] output = new Player[players.size()];
		for (int i = 0; i < players.size(); i++)
			output[i] = players.get(i);
		return output;
	}

	public static Player getPlayer(String name) {
		for (Player p : players)
			if (p.getName().trim().equalsIgnoreCase(name.trim()))
				return p;
		return null;
	}
	/**
	 * Adds the provided location to the list of player spawn points
	 * 
	 * @param location A {@link Location3D} object
	 */
	public static void addSpawnPoint(Location3D location) {
		spawnPoints.add(location);
	}

	/**
	 * Empties the list of player spawn points
	 */
	public static void clearSpawnPoints() {
		spawnPoints.clear();
	}

	/**
	 * @return One of the locations in the {@link #spawnPoints} list, selected at random
	 */
	public static Location3D getRandomSpawnPoint() {
		if (spawnPoints.size() == 0)
			return new Location3D(0, 0, 0, 0, 0, 0);
		return spawnPoints.get((int) (Math.random() * spawnPoints.size()));
	}
}
