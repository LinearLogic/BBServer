package com.veltro.blazingbarrels.server;

import java.io.File;

/**
 * Main class - contains {@link #main(String[]) launch method}
 * 
 * @author LinearLogic
 * @version 0.0.8
 */
public class BBServer {

	/**
	 * The utility class for management of the server's configuration
	 */
	private static Configuration config;

	/**
	 * The current version of the server software
	 */
	public static final String VERSION = "0.0.8";

	/**
	 * Program entry point
	 * 
	 * @param args ...
	 */
	public static void main(String[] args) {
		File configFile = new File ("config.txt"); // File is within the jar for simplicity in testing
		config = new Configuration(configFile);
		config.loadValues();
		
	}

	/**
	 * @return The server's {@link Configuration configuration manager}
	 */
	public static Configuration getConfig() {
		return config;
	}
}
