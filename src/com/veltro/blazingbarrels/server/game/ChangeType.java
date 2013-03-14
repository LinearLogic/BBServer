package com.veltro.blazingbarrels.server.game;

/**
 * The ChangeType enum contains various updates to a player's status that can occur during gameplay and should be sent
 * to clients connected to the server. Note that not all player variables trigger a change; for instance, clients are
 * not informed when a player toggles in and out of fly mode and god mode, as this has no bearing on the client's
 * operation (movement of other players and damage in general is handled server-side).
 * 
 * @author LinearLogic
 * @since 0.1.4
 */
public enum ChangeType {

	/**
	 * When a player is kicked from the server, this is the only change sent to clients.
	 */
	DISCONNECT_KICK,

	/**
	 * When a player's network connection times out, this is the only change sent to clients.
	 */
	DISCONNECT_TIMEOUT,

	/**
	 * When a player voluntarily disconnects from the server, this is the only change sent to clients.
	 */
	DISCONNECT_QUIT,

	/**
	 * This change signifies that a player's administrator status has been toggled, and the client is informed of this
	 * in order to change the color of the player's name in the list of connected players.
	 */
	ADMIN,

	/**
	 * This change indicates that a player's god mode status has been toggled, and the client is informed of this in
	 * order to change the color of the player's energy shield.
	 */
	GODMODE,

	/**
	 * This change means that a player's health has been altered. In the event that a player dies and respawns, both a
	 * health and location update will be registered.
	 */
	HEALTH,

	/**
	 * Denotes a change in the player's location. This includes both position (x-, y-, and z-coordinates) and rotation
	 * (yaw, pitch, and roll).
	 */
	LOCATION,

	/**
	 * Denotes a change in the player's visibility status. Invisible players' location and health values are not sent
	 * to the client, as the client neither renders vanished players nor displays their health levels.
	 */
	VISIBILITY;
}
