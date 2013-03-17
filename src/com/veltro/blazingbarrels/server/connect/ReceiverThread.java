package com.veltro.blazingbarrels.server.connect;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.veltro.blazingbarrels.server.connect.packet.BBPacket;
import com.veltro.blazingbarrels.server.connect.packet.Packet00AuthRequest;
import com.veltro.blazingbarrels.server.connect.packet.Packet20PlayerJoin;
import com.veltro.blazingbarrels.server.connect.packet.Packet22PlayerDisconnect;
import com.veltro.blazingbarrels.server.connect.packet.Packet30PlayerUpdate;
import com.veltro.blazingbarrels.server.connect.packet.Packet40WeaponFire;
import com.veltro.blazingbarrels.server.game.Location3D;
import com.veltro.blazingbarrels.server.game.WeaponType;

/**
 * A thread dedicated to receiving Datagram packets over a network socket. While running, this thread receives Datagram
 * packets, constructs BBPackets from them, and adds the resulting objects to the {@link #incomingPacketQueue}
 * 
 * @author LinearLogic
 * @since 0.0.4
 */
public class ReceiverThread extends Thread {

	/**
	 * Status flag for the loop. If set to false, causes the thread to complete its {@link #run()} method and terminate
	 */
	private boolean running = false;

	/**
	 * The internet socket over which to send packets in the {@link #packetQueue}
	 */
	private DatagramSocket socket = null;

	/**
	 * A queue (first in - first out list) of the packets to be sent over the internet
	 */
	public ConcurrentLinkedQueue<BBPacket> incomingPacketQueue;

	/**
	 * Passes the thread's name to the superclass constructor, attempts to open a DatagramSocket on the provided port,
	 * and initializes the {@link #incomingPacketQueue}
	 * 
	 * @param port The port on which to open a DatagramSocket to receive incoming DatagramPackets
	 * @throws SocketException Thrown if the server failed to bind to the provided port
	 * @throws SecurityException Thrown if a security manager blocks the creation of this thread (should never happen)
	 */
	public ReceiverThread(int port) throws SocketException, SecurityException {
		super("ReceiverThread");
		socket = new DatagramSocket(port);
		incomingPacketQueue = new ConcurrentLinkedQueue<BBPacket>();
	}

	/**
	 * The thread listens for incoming DatagramPackets arriving over the socket, casts them to BBPacket subclass
	 * objects, and adds them to the {@link #incomingPacketQueue}
	 */
	public void run() {
		while (running) {
			byte[] buffer = new byte[256];

			// Receive the packet
			DatagramPacket inbound = new DatagramPacket(buffer, buffer.length);
			try {
				socket.receive(inbound);
			} catch (IOException e) {
				continue;
			}

			
			// Unpack the packet's contents
			String[] data = new String(inbound.getData(), 0, inbound.getLength()).split("\\s+", 2);
			if (data.length <= 1) // No packet data beyond an ID has been supplied - discard packet
				continue;
			int id;
			try {
				id = Integer.parseInt(data[0]);
			} catch (NumberFormatException e) { // Invalid packet format - discard packet
				continue;
			}
			data = data[1].split("\\s+");
			BBPacket received = null;;

			switch(id) { // Only the id values of packets that the server should normally receive are handled
			
				// Packet00AuthRequest
				case 0:
					received = new Packet00AuthRequest(data[0], (data.length >= 2 ? data[1].trim() : ""),
							inbound.getAddress(), inbound.getPort());
					break;

				// Packet20PlayerJoin
				case 20:
					received = new Packet20PlayerJoin(data[0], (data.length >= 2 && data[1].equalsIgnoreCase("s") ?
							true : false), inbound.getAddress(), inbound.getPort());
					break;

				// Packet22PlayerDisconnect
				case 22:
					int reasonID = 0;
					if (data.length >= 2) {
						try {
							reasonID = Integer.parseInt(data[1]);
						} catch (NumberFormatException e) {
							break;
						}
						if (reasonID < 0 || reasonID > 2)
							reasonID = 0;
					}
					received = new Packet22PlayerDisconnect(data[0], reasonID, inbound.getAddress(), inbound.getPort());
					break;

				// Packet30PlayerUpdate
				case 30:
					if (data.length < 2 || data.length > 7) { // Too many updates - or none, have been specified
						break;
					}
					Packet30PlayerUpdate update = new Packet30PlayerUpdate(data[0], null, -1, false, false, false,
							false, inbound.getAddress(), inbound.getPort());
					for (int i = 1; i < data.length; i++) {
						String flag = data[i];
						switch(flag.charAt(0)) {
							case 'l':
								update.setLocation(new Location3D(flag.substring(1)));
								break;
							case 'h':
								try {
									update.setHealth(Integer.parseInt(flag.substring(1)));
								} catch (NumberFormatException e) {
									break;
								}
								break;
							case 'a':
								update.toggleAdminStatus();
								break;
							case 'f':
								update.toggleFlymode();
								break;
							case 'g':
								update.toggleGodMode();
								break;
							case 'v':
								update.toggleVisibility();
								break;
							default:
								break;
						}
					}
					update.updateData();
					received = update;
					break;

				// Packet40WeaponFire
				case 40:
					if (data.length != 3) // Invalid packet contents
						break;
					int weaponTypeID;
					try {
						weaponTypeID = Integer.parseInt(data[2]);
					} catch (NumberFormatException e) {
						break;
					}
					WeaponType type = WeaponType.getTypeFromID(weaponTypeID);
					if (type == null)
						break;
					received = new Packet40WeaponFire(data[0], new Location3D(data[1]), type, inbound.getAddress(),
							inbound.getPort());
					break;

				default:
					break;
			}
			if (received != null)
				incomingPacketQueue.add(received);
		}
		socket.close();
	}

	/**
	 * Causes the main loop in the {@link #run()} method to exit; as a result, the thread completes its execution. Note
	 * that this is not a guaranteed way to instantly terminate the thread, as its execution pauses until it receives a
	 * packet. This is not really an issue, however, as this method is only called when the server in its entirety is
	 * shutting down.
	 */
	public void terminate() {
		running = false;
	}
}
