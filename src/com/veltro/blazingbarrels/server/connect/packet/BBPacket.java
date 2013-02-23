package com.veltro.blazingbarrels.server.connect.packet;

import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * The superclass for specifying custom UDP packets
 * 
 * @author LinearLogic
 * @since 0.0.2
 */
public class BBPacket {

	/**
	 * The integer ID corresponding to the type of packet
	 */
	private int ID;

	/**
	 * The String of data to be broken down into bytes and inserted into a DatagramPacket for transmission
	 */
	private String data;

	/**
	 * The IP address of the packet's destination
	 */
	private InetAddress address;

	/**
	 * The port (on the {@link #address}) of the packet's destination
	 */
	private int port;

	/**
	 * Constructor - called by the BBPacket subclasses during their construction
	 * 
	 * @param packetID The packet's {@link #ID}
	 * @param data The packet's {@link #data}
	 * @param address The IP address of the packet's destination
	 * @param port The port of the packet's destination
	 */
	public BBPacket(int packetID, String data, InetAddress address, int port) {
		this.ID = packetID;
		this.data = data;
		this.address = address;
		this.port = port;
	}

	/**
	 * Constructs a DatagramPacket based on the BBPacket's attributes.
	 * 
	 * @return The resulting DatagramPacket
	 */
	public DatagramPacket generatePacket() {
		byte[] buffer = new String(ID + " " + data).getBytes();
		return new DatagramPacket(buffer, buffer.length, address, port);
	}
}
