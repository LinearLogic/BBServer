package com.veltro.blazingbarrels.server.connect;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.veltro.blazingbarrels.server.connect.packet.BBPacket;
import com.veltro.blazingbarrels.server.connect.packet.Packet0AuthRequest;

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
	 * The thread listens for incoming packets arriving over the socket, casts them to BBPacket subclass objects, and
	 * adds them to the {@link #incomingPacketQueue}
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
			String data[] = new String(inbound.getData(), 0, inbound.getLength()).split("\\s+", 2);
			int id;
			try {
				id = Integer.parseInt(data[0]);
			} catch (NumberFormatException e) { // Invalid packet format - discard packet
				continue;
			}
			BBPacket received = null;;

			switch(id) { // Only the id values of packets that a client should normally receive are handled
				case 0:
					if (data.length == 2) { // A username but no password has been specified
						received = new Packet0AuthRequest(data[1], null, inbound.getAddress(), inbound.getPort());
						break;
					}
					if (data.length == 3) { // Both a username and password have been specified
						received = new Packet0AuthRequest(data[1], data[2], inbound.getAddress(), inbound.getPort());
						break;
					}
					received = null; // Invalid packet contents
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
	 * DatagramPacket
	 */
	public void terminate() {
		running = false;
	}
}
