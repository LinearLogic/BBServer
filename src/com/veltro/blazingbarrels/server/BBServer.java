package com.veltro.blazingbarrels.server;

import java.io.File;
import java.net.SocketException;

import com.veltro.blazingbarrels.server.connect.PacketManager;
import com.veltro.blazingbarrels.server.connect.ReceiverThread;
import com.veltro.blazingbarrels.server.connect.SenderThread;

/**
 * Main class - contains {@link #main(String[]) launch method}
 * 
 * @author LinearLogic
 * @version 0.2.5
 */
public class BBServer {

	/**
	 * The current version of the server software
	 */
	public static final String VERSION = "0.2.5";

	/**
	 * The program status flag - if set to 'false', causes the program to terminate
	 */
	private static boolean running = false;

	/**
	 * The utility class for management of the server's {@link Configuration configuration}
	 */
	private static Configuration config;

	/**
	 * The server's {@link PacketManager}, which is responsible for the handling of received packets, the updating of
	 * the server, and the creation of response packets to be sent to clients connected to the server.
	 */
	private static PacketManager pm;

	/**
	 * The thread responsible for the transmission of UDP packets over the network 
	 */
	private static SenderThread sender;

	/**
	 * The thread responsible for the receipt of UDP packets sent over the network
	 */
	private static ReceiverThread receiver;

	/**
	 * The thread responsible for parsing console input
	 */
	private static InputThread input;

	/**
	 * Program entry point
	 * 
	 * @param args ...
	 */
	public static void main(String[] args) {
		running = true;

		// Preliminary stuff (config handler and packet manager setup)
		System.out.println("Welcome to BBServer " + VERSION + ", the portal for Blazing Barrels multiplayer!");
		File configFile = new File ("config.txt"); // File is within the jar for simplicity in testing
		config = new Configuration(configFile);
		config.loadValues();
		pm = new PacketManager();

		// Set up threads:
		try {
			sender = new SenderThread();
		} catch (SocketException e) {
			System.err.println("Failed to find an available port for packet transmission. Stopping the server...");
			return;
		} catch (SecurityException e) {
			e.printStackTrace();
			return;
		}
		try {
			receiver = new ReceiverThread(config.getPort());
		} catch (SocketException e) {
			System.err.println("Failed to bind to port " + config.getPort() + " for packet receipt. Is it in use by " +
					"another program?\nStopping the server...");
			return;
		} catch (SecurityException e) {
			e.printStackTrace();
			return;
		}
		input = new InputThread();

		// Launch threads:
		sender.start();
		receiver.start();
		input.start();

		// Main loop
		while(running) {
			pm.runCycle();
		}

		// Cleanup:
		System.out.println("Saving the server configuration...");
		config.saveValues();
		sender.terminate();
		receiver.terminate();
		System.out.print("\nAdios!");
	}

	/**
	 * @return The server's {@link Configuration configuration manager}
	 */
	public static Configuration getConfig() {
		return config;
	}

	/**
	 * @return The server's {@link #pm packet manager}
	 */
	public static PacketManager getPacketManager() {
		return pm;
	}

	/**
	 * @return The server's {@link #sender} thread
	 */
	public static SenderThread getSenderDaemon() {
		return sender;
	}

	/**
	 * @return The server's {@link #receiver} thread
	 */
	public static ReceiverThread getReceiverDaemon() {
		return receiver;
	}

	/**
	 * @return The server's {@link #input input thread}
	 */
	public static InputThread getInputThread() {
		return input;
	}

	/**
	 * Causes the server's main logic loop to exit by setting the value of the {@link #running} flag to 'false'
	 */
	public static void terminate() {
		System.out.println("Stopping the server...");
		running = false;
	}
}
