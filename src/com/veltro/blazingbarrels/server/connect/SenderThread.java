package com.veltro.blazingbarrels.server.connect;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.veltro.blazingbarrels.server.connect.packet.BBPacket;

/**
 * A thread dedicated to sending Datagram packets to network addresses. While running, this thread waits for 
 * {@link BBPacket packets} to be added to the {@link #outgoingPacketQueue}
 * 
 * @author LinearLogic
 * @since 0.0.3
 */
public class SenderThread extends Thread {

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
	public ConcurrentLinkedQueue<BBPacket> outgoingPacketQueue;

	/**
	 * Passes the thread's name to the superclass constructor and initializes the {@link #outgoingPacketQueue}
	 * 
	 * @throws SocketException Thrown if the server failed to bind the {@link #socket} to an available port
	 * @throws SecurityException Thrown if a security manager blocks the creation of this thread (should never happen)
	 */
	public SenderThread() throws SocketException, SecurityException {
		super("SenderThread");
		socket = new DatagramSocket();
		outgoingPacketQueue = new ConcurrentLinkedQueue<BBPacket>();
	}

	/**
	 * The thread listens for packets to be added to the {@link #outgoingPacketQueue}, and once they are, it sends them over
	 * the network to their destinations
	 */
	public void run() {
		while (running) {
			if (!outgoingPacketQueue.isEmpty()) {
				try {
					socket.send(outgoingPacketQueue.poll().generatePacket());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		socket.close();
	}

	/**
	 * Causes the main loop in the {@link #run()} method to exit; as a result, the thread completes its execution
	 */
	public void terminate() {
		running = false;
	}
}
