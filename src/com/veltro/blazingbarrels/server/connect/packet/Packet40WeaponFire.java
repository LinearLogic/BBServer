package com.veltro.blazingbarrels.server.connect.packet;

import java.net.InetAddress;

import com.veltro.blazingbarrels.server.game.Location3D;
import com.veltro.blazingbarrels.server.game.WeaponType;

/**
 * This packet is sent from a client to the server when the client fires a weapon, and from the server to all connected
 * clients shortly thereafter in order for the clients to render the shot. The packet is the same in both cases, and
 * contains the starting point and direction of the shot's trajectory and the type of weapon being used.<p>
 * 
 * If the server determines that the shot being fired hits a player, that player will be damaged. If the player is then
 * killed, the server will broadcast the occurrence along with the name of the player that fired the fatal shot.<p>
 * 
 * This packet is both sent and received by the server.
 * 
 * @author LinearLogic
 * @since 0.2.12
 */
public class Packet40WeaponFire extends BBPacket {

	/**
	 * The name of the player that fired the shot
	 */
	private String username;

	/**
	 * The trajectory of the shot that was fired, represented as a ray starting at the location of the player firing
	 * the shot and continuing in the direction given by the rotation values of the location.
	 */
	private Location3D trajectoryRay;

	/**
	 * The {@link WeaponType type} of weapon being fired
	 */
	private WeaponType type;

	/**
	 * Constructs the {@link BBPacket} superclass with the ID of this packet (30), its data rendered as a string, and
	 * its Internet destination address. Initializes all class fields.
	 * 
	 * @param username The name of the player that fired the shot
	 * @param trajectory The shot's {@link #trajectoryRay}
	 * @param type The {@link WeaponType type} of the weapon being fired
	 * @param address The packet's source/destination IP address
	 * @param port The port on the above address
	 */
	public Packet40WeaponFire(String username, Location3D trajectory, WeaponType type, InetAddress address, int port) {
		super(40, (username == null ? "" : username + " ") + trajectory.toString() + " " + type.ID, address, port);
		this.username = username;
		trajectoryRay = trajectory;
		this.type = type;
	}

	public void handle() {
		
	}

	/**
	 * @return The name of the player that fired the shot
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return The shot's {@link #trajectoryRay}
	 */
	public Location3D getTrajectoryRay() {
		return trajectoryRay;
	}

	/**
	 * @return The {@link WeaponType type} of weapon being fired
	 */
	public WeaponType getWeaponType() {
		return type;
	}
}
