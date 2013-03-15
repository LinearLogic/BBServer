package com.veltro.blazingbarrels.server.connect.packet;

import java.net.InetAddress;

import com.veltro.blazingbarrels.server.game.Location3D;
import com.veltro.blazingbarrels.server.game.Player;

/**
 * This packet is sent by a client to update the server's copy of the client's {@link Player}, and is broadcasted by
 * the server so that all connected clients can update their copies of the that same player.<p>
 * 
 * Due to the fact that not all attributes of a player may change during any given cycle, it is customary for several
 * fields in a Packet30PlayerUpdate object to be null (for objects) or initialized to default values (for primitives).
 * <p>
 * This packet is both sent and received by the server.
 * 
 * @author LinearLogic
 * @since 0.2.8
 */
public class Packet30PlayerUpdate extends BBPacket {

	/**
	 * The name of the player whose attributes are being updated
	 */
	private String username;

	/**
	 * The player's new location (null if it has not changed)
	 */
	private Location3D location;

	/**
	 * The player's new health level (-1 if it has not changed)
	 */
	private int health;

	/**
	 * Indicates whether the player's administrator status has changed ('true' if so)
	 */
	private boolean toggleAdmin;

	/**
	 * Indicates whether the player has been toggled in or out of flymode ('true' if so)
	 */
	private boolean toggleFlyMode;

	/**
	 * Indicates whether the player has been toggled in or out of godmode ('true'if so)
	 */
	private boolean toggleGodMode;

	/**
	 * Indicates whether the player's visibility has been changed ('true' if so)
	 */
	private boolean toggleVisibility;

	/**
	 * Default constructor, intended to be followed up with various calls to specify the packet's data and address.
	 * This constructor calls the {@link #Packet30PlayerUpdate(String, Location3D, int, boolean, boolean, boolean,
	 * boolean, InetAddress, int) complete constructor}, passing null/default values.
	 * 
	 * @param username The name of the player whose data is being updated
	 */
	public Packet30PlayerUpdate(String username) {
		this(username, null, -1, false, false, false, false, null, 0);
	}

	/**
	 * Constructs the {@link BBPacket} superclass with the ID of this packet (30), its data rendered as a string, and
	 * its Internet destination address. Initializes all class fields.
	 * 
	 * @param username The name of the player whose data is being updated
	 * @param newLocation The player's new {@link Location3D location} (null if it has not changed)
	 * @param newHealth The player's new health level (-1 if it has not changed)
	 * @param toggleAdmin Whether the player's administrator status should be toggled
	 * @param toggleFlyMode Whether the player's ability to fly should be toggled
	 * @param toggleGodMode Whether the player's invincibility should be toggled
	 * @param toggleVisibility Whether the player's visibility should be toggled
	 * @param address The source/destination IP address of the packet
	 * @param port The port on the above address
	 */
	public Packet30PlayerUpdate(String username, Location3D newLocation, int newHealth, boolean toggleAdmin,
			boolean toggleFlyMode, boolean toggleGodMode, boolean toggleVisibility, InetAddress address, int port) {
		super(30, username + (newLocation != null ? " l" + newLocation.toString() : "") + (newHealth > -1 ? " h" +
			newHealth : "") + (toggleAdmin ? " a" : "") + (toggleFlyMode ? " f" : "") + (toggleGodMode ? " g" : "") +
			(toggleVisibility ? " v" : ""), address, port);
		this.username = username;
		location = newLocation;
		health = newHealth;
		this.toggleAdmin = toggleAdmin;
		this.toggleFlyMode = toggleFlyMode;
		this.toggleGodMode = toggleGodMode;
		this.toggleVisibility = toggleVisibility;
	}

	public void handle() {
		
	}

	/**
	 * This method should be called after updating fields in this class in order to refresh the contents of the
	 * {@link BBPacket#data} string.
	 */
	public void updateData() {
		data = username + (location != null ? " l" + location.toString() : "") + (health > -1 ? " h" +
				health : "") + (toggleAdmin ? " a" : "") + (toggleFlyMode ? " f" : "") + (toggleGodMode ? " g" : "") +
				(toggleVisibility ? " v" : "");
	}

	/**
	 * @return The username of the player whose status is being updated
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return The player's updated {@link #location}, if any
	 */
	public Location3D getLocation() {
		return location;
	}

	/**
	 * Specifies the player's {@link #location}
	 * 
	 * @param location A {@link Location3D} object
	 */
	public void setLocation(Location3D location) {
		this.location = location;
	}

	/**
	 * @return The player's {@link #health} level
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * Specifies the player's {@link #health}
	 * 
	 * @param newHealth A whole number
	 */
	public void setHealth(int newHealth) {
		health = newHealth;
	}

	/**
	 * @return The {@link #toggleAdmin} value
	 */
	public boolean isAdminStatusToggled() {
		return toggleAdmin;
	}

	/**
	 * Specifies that the player's admin status should be toggled
	 */
	public void toggleAdminStatus() {
		toggleAdmin = true;
	}

	/**
	 * @return The {@link #toggleFlyMode} value
	 */
	public boolean isFlyModeToggled() {
		return toggleFlyMode;
	}

	/**
	 * Specifies that the player's ability to fly should be toggled
	 */
	public void toggleFlymode() {
		toggleFlyMode = true;
	}

	/**
	 * @return The {@link #toggleGodMode} value
	 */
	public boolean isGodeModeToggled() {
		return toggleGodMode;
	}

	/**
	 * Specifies that the player's invincibility should be toggled
	 */
	public void toggleGodMode() {
		toggleGodMode = true;
	}

	/**
	 * @return The {@link #toggleVisibility} value
	 */
	public boolean isVisibilityToggled() {
		return toggleVisibility;
	}

	/**
	 * Specifies that the player's visibility should be toggled
	 */
	public void toggleVisibility() {
		toggleVisibility = true;
	}
}
