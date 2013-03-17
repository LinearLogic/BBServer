package com.veltro.blazingbarrels.server.connect.packet;

import java.net.InetAddress;

import com.veltro.blazingbarrels.server.BBServer;
import com.veltro.blazingbarrels.server.game.Location3D;
import com.veltro.blazingbarrels.server.game.Player;
import com.veltro.blazingbarrels.server.game.Vector3D;
import com.veltro.blazingbarrels.server.game.WeaponType;
import com.veltro.blazingbarrels.server.game.World;

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

	/**
	 * Checks to ensure that the shooter (the player with the provided {@link #username}) is legitimate, and then
	 * handles the firing of the weapon. If the weapon is a nuke, all players except for the shooter are killed. If the
	 * weapon is an EMP, no handling is done server-side. If the weapon is of any other type, the weapon's trajectory
	 * is checked for collision with a player using mathematical operations on {@link Vector3D} objects. If a collision
	 * occurs, the target player is damaged according to the {@link WeaponType}'s damage and damage dropoff values. If
	 * the player is killed by the shot, the occurrence is printed to console.
	 */
	public void handle() {
		Player shooter = World.getPlayer(username);
		if (shooter == null)
			return;
		shooter.setClientAddress(address);
		shooter.setClientPort(port);

		// Handle abnormal weapons
		if (type.equals(WeaponType.NUKE)) {
			BBServer.getPacketManager().broadcastPacket(this);
			for (Player p : World.getPlayers())
				if (p.getName() != username)
					p.damage(p.getHealth() + 1); // Make sure no one survives the blast
			System.out.println(shooter.getName() + " used activated the doomsday device!");
			return;
		}
		if (type.equals(WeaponType.EMP)) {
			BBServer.getPacketManager().broadcastPacket(this);
			return;
		}

		// Handle linear trajectory weapons:
		Vector3D direction = new Vector3D((float) Math.cos(trajectoryRay.getYaw()),
				(float) Math.sin(trajectoryRay.getYaw()), (float) Math.sin(trajectoryRay.getPitch())), // Unit vector
				source = new Vector3D(trajectoryRay.getX(), trajectoryRay.getY(), trajectoryRay.getZ()); // Source
		for (Player p : World.getPlayers()) {
			if ((direction.getX() >= 0 && p.getLocation().getX() + Player.SHIELD_RADIUS < source.getX()) ||
			(direction.getY() >= 0 && p.getLocation().getY() + Player.SHIELD_RADIUS < source.getY()) ||
			(direction.getZ() >= 0 && p.getLocation().getZ() + Player.SHIELD_RADIUS < source.getZ()))
				continue;
			// Player is a potential target
			Vector3D target = new Vector3D(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ());
			if (Math.sqrt(Math.pow(direction.dot(source.add(target)), 2) + (Player.SHIELD_RADIUS ^ 2) -
					source.add(target.negate()).square()) >= 0) { // The shot has hit the player
				p.damage(type.damage - (int) (type.damageDropoff * trajectoryRay.distanceTo(p.getLocation()) / 100.0));
				if (p.getHealth() == BBServer.getConfig().getHealthCap()) { // The shot killed the player
					// TODO: broadcast packet explaining that the player was killed by the shooter
					System.out.println(p.getName() + "'s ship was destroyed by " + shooter.getName() + " using a " +
							type.toString());
				}
			}
			
		}
		BBServer.getPacketManager().broadcastPacket(this);
		
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
