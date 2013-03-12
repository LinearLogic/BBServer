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
	 * This change overrides all others and is the only change sent when a player disconnects. Upon being notified that
	 * a player has disconnected, the client removes that player from its list of online players and displays the
	 * disconnect message.
	 */
	DISCONNECT,

	/**
	 * This change signifies that a player's administrator status has been toggled, and the client is informed of this
	 * in order to change the color of the player's name in the list of connected players.
	 */
	ADMIN,

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
