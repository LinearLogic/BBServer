package com.veltro.blazingbarrels.server.connect.packet;

import java.net.InetAddress;

import com.veltro.blazingbarrels.server.BBServer;
import com.veltro.blazingbarrels.server.game.Player;

/**
 * This packet is sent to a client to provide it with all the information it needs to create a local copy of the game
 * world and all its contents. Due to size limit on the {@link BBPacket} payload, the server will occasionally send
 * multiple instances of this packet in order to provide a complete snapshot (whether this happens is determined based
 * on the return value of the {@link #addPlayerSnapshot(Player)} method each time a player's information is appended to
 * the packet's {@link BBPacket#data}).<p>
 * 
 * This packet is only ever sent by the server.
 * 
 * @author LinearLogic
 * @since 0.2.6
 */
public class Packet10ServerSnapshot extends BBPacket {

	/**
	 * Constructs the {@link BBPacket} superclass with the ID of this packet (10), its data rendered as a string, and
	 * its Internet destination address. Initializes all class fields.
	 * 
	 * @param sendServerInfo Whether to send the client the necessary info about the server (this info is typically
	 * only sent in the first member of a series of Packet6ServerSnapshot packets.
	 * @param address The IP address of the client being sent the snapshot
	 * @param port The port on the above address
	 */
	public Packet10ServerSnapshot(boolean sendServerInfo, InetAddress address, int port) {
		super(10, (sendServerInfo ? "s." + BBServer.getConfig().getWorldRadius() + "." +
				BBServer.getConfig().getHealthCap() : ""), address, port);
	}

	/**
	 * This packet is never received by the server, so it is not handled.
	 */
	public void handle() { }

	/**
	 * Attempts to add the provided user's relevant data to the packet. This method ensures
	 * @param player The player whose data to add to the packet
	 * @return 'true' if the player's data is successfully added to the packet, 'false' if the operation failed due to
	 * the packet payload size limit being reached
	 */
	public boolean addPlayerSnapshot(Player player) {
		String toAppend = player.generateSnapshotString();
		if (toAppend.length() + data.length() > 210) // Packet data will be truncated if this player's info is included
			return false;
		data += toAppend;
		return true;
	}
}