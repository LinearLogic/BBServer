package com.veltro.blazingbarrels.server.connect;

import java.net.InetAddress;

import com.veltro.blazingbarrels.server.BBServer;
import com.veltro.blazingbarrels.server.connect.packet.Packet02DeauthWarning;
import com.veltro.blazingbarrels.server.connect.packet.Packet20PlayerJoin;

/**
 * A task thread used to send {@link Packet02DeauthWarning} packets to a client that has been authorized to join but
 * has not responded with a {@link Packet20PlayerJoin}. If the maximum number of {@link #warnings} are sent, with a
 * specified {@link #timeout} in between each, the player is disconnected from the server.
 * 
 * @author LinearLogic
 * @since 0.3.0
 */
public class DeauthTask extends Thread {

	/**
	 * The name of the player whose client is the recipient of the {@link Packet02DeauthWarning} packets being sent,
	 * and who will be removed from the server if a response is not received within the allotted time.
	 */
	private String name;

	/**
	 * The IP address of the client being sent deauthorization warnings
	 */
	private InetAddress address;

	/**
	 * The port on the recipient client's {@link #address}
	 */
	private int port;

	/**
	 * The number of {@link Packet02DeauthWarning} packets to send to the client before disconnecting it
	 */
	private int warnings;

	/**
	 * The amount of time (in milliseconds) to wait in between {@link Packet02DeauthWarning} dispatches
	 */
	private int timeout;

	/**
	 * Constructs the Thread superclass and initializes the {@link #player}, {@link #warnings}, and {@link #timeout}
	 * fields to the provided values.
	 * 
	 * @param playerName The name of the player receiving the deauthorization warning
	 * @param address The IP address of the client of the player the above name
	 * @param port The port on the above address
	 * @param warnings The number of warnings to send the player's client before deauthorizing the player
	 * @param timeout The amount of time, in milliseconds, between warnings being sent
	 */
	public DeauthTask(String playerName, InetAddress address, int port, int warnings, int timeout) {
		super("DeauthTask");
		name = playerName;
		this.address = address;
		this.port = port;
		this.warnings = warnings;
		this.timeout = timeout;
	}

	/**
	 * Pauses for the length of the {@link #timeout} field and then sends a {@link Packet02DeauthWarning}. This process
	 * is repeated as many times as is specified in the {@link #warnings} field, and then the player is disconnected
	 * from the server for timing out.
	 */
	public void run() {
		Packet02DeauthWarning packet = new Packet02DeauthWarning(name, address, port);
		for (int i = 0; i < warnings; i++) {
			try {
				wait(timeout);
			} catch (InterruptedException e) { // The client has sent a PlayerJoin packet; cease the deauth warnings
				System.err.println("Stopping the deauth task for player " + name + "!"); // for debugging
				return;
			}
			BBServer.getSenderDaemon().outgoingPacketQueue.add(packet);
		}
	}

	/**
	 * Starts the task thread
	 * 
	 * @return The DeauthTask object running this method
	 */
	public DeauthTask startTask() {
		super.start();
		return this;
	}

	/**
	 * @return The {@link #name} of the task's player
	 */
	public String getPlayerName() {
		return name;
	}

	/**
	 * @return The {@link #address} registered with the task
	 */
	public InetAddress getClientAddress() {
		return address;
	}

	/**
	 * @return The port on the {@link #address} registered with the task
	 */
	public int getClientPort() {
		return port;
	}
}
