package com.veltro.blazingbarrels.server;

import java.util.Scanner;

import com.veltro.blazingbarrels.server.game.Player;
import com.veltro.blazingbarrels.server.game.World;

/**
 * The InputThread is dedicated to listening for and parsing console input, enabling the main thread to run without
 * having to pause and wait for commands to be entered.
 * 
 * @author LinearLogic
 * @since 0.0.10
 */
public class InputThread extends Thread {

	/**
	 * Default constructor... nothing to see here... move along now... why are you still reading this?!
	 */
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
			if (name.equals("/help") || name.equals("/?")) {
				System.out.println("[]===[]===[Commands]===[]===[]\n" +
						"/info - displays configuration info\n" +
						"/list - lists online players\n" +
						"/stop - terminates the server\n" +
						"/version - displays the version of BBServer currently being run\n");
				continue;
			}
			if (name.equals("/info")) {
				System.out.println("[]===[]===[Config Info]===[]===[]\nConnection:\n" +
						"\tPort number: " + BBServer.getConfig().getPort() + "\n" +
						"\tServer password: " + BBServer.getConfig().getPassword() + "\n" +
						"\tPlayer slots: " + BBServer.getConfig().getPlayerCap() + "\nIn-game:\n" +
						"\tPlayer health cap: " + BBServer.getConfig().getHealthCap() + "\n" +
						"\tWorld radius: " + BBServer.getConfig().getWorldRadius() + "\n");
				continue;
			}
			if (name.equals("/list")) {
				 System.out.println("Connected players:");
				 for (Player p : World.getPlayers())
					 System.out.println(p.getName());
				 System.out.println();
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
