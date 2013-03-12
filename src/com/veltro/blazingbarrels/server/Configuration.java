package com.veltro.blazingbarrels.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import com.veltro.blazingbarrels.server.connect.ReceiverThread;
import com.veltro.blazingbarrels.server.connect.SenderThread;
import com.veltro.blazingbarrels.server.connect.packet.Packet0AuthRequest;

/**
 * Configuration manager - provides methods for accessing and altering nodes in the {@link #configFile}, such as the
 * server's {@link #playerCap}, {@link #port}, and {@link #password}.
 * 
 * @author LinearLogic
 * @since 0.0.8
 */
public class Configuration {

	/**
	 * The maximum health value a player can have. When a player spawns, their health level is set to this value.<p>
	 * Like the other config values, the health cap cannot be changed without restarting the server, so it
	 * does not have a setter method.
	 */
	private int healthCap;

	/**
	 * The maximum number of players allowed on the server simultaneously. Note that this limit does not apply to
	 * server administrators.<p>
	 * Like the other config values, the player cap cannot be changed without restarting the server, so it
	 * does not have a setter method.
	 */
	private int playerCap;

	/**
	 * The port number on which to open a socket for use by the {@link ReceiverThread} (a port does not need
	 * to be specified for use by the {@link SenderThread}). <p>
	 * Like the other config values, the port cannot be changed without restarting the server, so it does not
	 * have a setter method.
	 */
	private int port;

	/**
	 * The radius, in pixels, of the cylindrical border that encloses the in-game world. This value is supplied to
	 * clients connected to the server in order for them to handle collisions with the world border.<p>
	 * Like the other config values, the world radius cannot be changed without restarting the server, so it does not
	 * have a setter method.
	 */
	private int worldRadius;

	/**
	 * The password that users attempting to connect to the server must provide during authorization. If this field is
	 * null or is an empty String, password checking will not be performed on incoming {@link Packet0AuthRequest}s.<p>
	 * Like the other config values, the password cannot be changed without restarting the server, so it does
	 * not have a setter method.
	 */
	private String password;

	/**
	 * The configFile stores all of the server's configuration values ({@link #playerCap}, {@link #port}, etc.)
	 */
	private File configFile;

	/**
	 * The constructor does not load any values from the provided configuration file; it merely initializes the
	 * {@link #configFile reference to the file}
	 * 
	 * @param configFile The File containing the configuration values
	 */
	public Configuration(File configFile) {
		this.configFile = configFile;
	}

	/**
	 * Calls the {@link #loadDefaults()} method and then populates the configuration value fields ({@link #playerCap},
	 * {@link #port}, etc.) based on data stored in the {@link #configFile}. Lastly, the {@link #saveValues()} method
	 * is called to update the config file, removing any extraneous lines it may contain.
	 */
	public void loadValues() {
		loadDefaults();
		Scanner sc;
		try {
			sc = new Scanner(configFile);
		} catch (FileNotFoundException e) {
			System.out.println("Failed to locate the config file. Creating a default one for you...");
			saveValues();
			return;
		}
		while (sc.hasNext()) {
			String[] data = sc.nextLine().split("\\s+", 2);
			if (data[0].equalsIgnoreCase("health-cap:")) {
				try {
					healthCap = Integer.parseInt(data[1]);
				} catch (NumberFormatException e) {
					System.err.println("Invalid health cap in the config file: not a number. Using default value...");
				}
				continue;
			}
			if (data[0].equalsIgnoreCase("password:")) {
				password = data.length == 2 ? data[1].trim() : null;
				continue;
			}
			if (data[0].equalsIgnoreCase("player-cap:") && data.length == 2) {
				try {
					playerCap = Integer.parseInt(data[1]);
				} catch (NumberFormatException e) {
					System.err.println("Invalid player cap in the config file: not a number. Using default value...");
				}
				continue;
			}
			if (data[0].equalsIgnoreCase("port:") && data.length == 2) {
				try {
					port = Integer.parseInt(data[1]);
				} catch (NumberFormatException e) {
					System.err.println("Invalid port value in the config file: not a number. Using default value...");
				}
				continue;
			}
			if (data[0].equalsIgnoreCase("world-radius:") && data.length == 2) {
				try {
					worldRadius = Integer.parseInt(data[1]);
				} catch (NumberFormatException e) {
					System.err.println("Invalid world radius in the config file: not a number. Using default value...");
				}
				continue;
			}
		}
		saveValues();
	}

	/**
	 * Writes the configuration values ({@link #playerCap}, {@link #port}, etc.) to the {@link #configFile}
	 * 
	 * @return 'false' if an error occurred during the method's execution that prevented the config values from being
	 * saved, else 'true'
	 */
	public void saveValues() {
		FileWriter fw;
		try {
			fw = new FileWriter(configFile);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Critical error attempting to save the server configuration. Stopping the server...");
			System.exit(0);
			return;
		}
		PrintWriter pw = new PrintWriter(fw);
		pw.println("Health-cap: " + healthCap);
		pw.println("Password:" + (password == null || password.equals("") ? "" : " " + password));
		pw.println("Player-cap: " + playerCap);
		pw.println("Port: " + port);
		pw.println("World-radius: " + worldRadius);
		pw.close();
		try {
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Populates the configuration value fields ({@link #playerCap}, {@link #port}, etc.) with default values. This
	 * method is called before loading values from the {@link #configFile}, so that all fields not specified in the
	 * file will still have values assigned.
	 */
	private void loadDefaults() {
		healthCap = 100;
		playerCap = 5;
		port = 7430;
		password = null;
		worldRadius = 500;
	}

	/**
	 * @return The server's {@link #healthCap} value
	 */
	public int getHealthCap() {
		return healthCap;
	}

	/**
	 * @return The server's {@link #playerCap} value
	 */
	public int getPlayerCap() {
		return playerCap;
	}

	/**
	 * @return The {@link #port} on which to receive packets from BlazingBarrels clients
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return The server's {@link #worldRadius}
	 */
	public int getWorldRadius() {
		return worldRadius;
	}

	/**
	 * @return The server's {@link #password}
	 */
	public String getPassword() {
		return password == null ? "" : password;
	}
}
