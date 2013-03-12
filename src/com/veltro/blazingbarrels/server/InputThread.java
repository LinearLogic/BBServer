package com.veltro.blazingbarrels.server;

import java.util.Scanner;

/**
 * The InputThread is dedicated to listening for and parsing console input, enabling the main thread to run without
 * having to pause and wait for commands to be entered.
 * 
 * @author LinearLogic
 * @since 0.0.10
 *
 */
public class InputThread extends Thread {

	public InputThread() {
		super("InputThread");
	}

	/**
	 * Uses a Scanner to listen for console input and parses it as a command consisting of an array of String arguments
	 */
	public void run() {
		Scanner sc = new Scanner(System.in);
		while (true) {
			String[] command = sc.nextLine().trim().split("\\s+");
			String name = command[0].toLowerCase();
			if (name.equals("/help")) {
				System.out.println("[]===[]===[Commands]===[]===[]\n" +
						"/info - displays configuration info\n" +
						"/stop - terminates the server\n" +
						"/version - displays the version of BBServer currently being run\n");
				continue;
			}
			if (name.equals("/info")) {
				System.out.println("[]===[]===[Config Info]===[]===[]\n" +
						"Server password: " + BBServer.getConfig().getPassword() + "\n" +
						"Player slots: " + BBServer.getConfig().getPlayerCap() + "\n" +
						"Port number: " + BBServer.getConfig().getPort() + "\n");
				continue;
			}
			if (name.equals("/stop")) {
				BBServer.terminate();
				break;
			}
			if (name.equals("/version")) {
				System.out.println("You are running BBServer version " + BBServer.VERSION + " by LinearLogic\n");
				continue;
			}
			System.out.println("Input not recognized. Type /help for a list of available commands.\n");
		}
		sc.close();
	}
}
