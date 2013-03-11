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
	 * The maximum number of players allowed on the server simultaneously. Note that this limit does not apply to
	 * server administrators.<p>
	 * Note the lack of a setter method for this value; once in operation, the server's player cap cannot be changed
	 * without restarting the program.
	 */
	private int playerCap;

	/**
	 * The port number on which to open a socket for use by the {@link ReceiverThread} (a port does not need
	 * to be specified for use by the {@link SenderThread}). <p>
	 * Note the lack of a setter method for this value; once in operation, the server's port cannot be changed without
	 * restarting the program.
	 */
	private int port;

	/**
	 * The password that users attempting to connect to the server must provide during authorization. If this field is
	 * null or is an empty String, password checking will not be performed on incoming {@link Packet0AuthRequest}s.<p>
	 * Like the {@link #playerCap} and {@link #port} values, the server's password cannot be changed without restarting
	 * the program, so it does not have a setter method.
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
			System.err.println("Failed to save data to the configuration - could not locate the config file.");
			return;
		}
		while (sc.hasNext()) {
			String[] data = sc.nextLine().split("\\s+", 2);
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
		}
		saveValues();
	}

	/**
	 * Writes the configuration values ({@link #playerCap}, {@link #port}, etc.) to the {@link #configFile}
	 */
	public void saveValues() {
		FileWriter fw;
		try {
			fw = new FileWriter(configFile);
		} catch (IOException e) {
			System.err.println("Failed to save data to the configuration - could not locate the config file.");
			return;
		}
		PrintWriter pw = new PrintWriter(fw);
		pw.println("Password:" + (password == null || password.equals("") ? "" : " " + password));
		pw.println("Player-cap: " + playerCap);
		pw.println("Port: " + port);
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
		playerCap = 5;
		port = 7430;
		password = null;
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
	 * @return The server's {@link #password}
	 */
	public String getPassword() {
		return password;
	}
}
