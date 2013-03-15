package com.veltro.blazingbarrels.server.connect.packet;

import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * The superclass for specifying custom UDP packets
 * 
 * @author LinearLogic
 * @since 0.0.2
 */
public abstract class BBPacket implements Comparable<BBPacket> {

	/**
	 * The integer ID corresponding to the type of packet. As well as serving to identify an incoming packet, the ID
	 * determines the packet's priority in the {@link #compareTo(BBPacket)} method.
	 */
	protected int ID;

	/**
	 * The String of data to be broken down into bytes and inserted into a DatagramPacket for transmission
	 */
	protected String data;

	/**
	 * The IP address of the packet's destination
	 */
	protected InetAddress address;

	/**
	 * The port (on the {@link #address}) of the packet's source or destination
	 */
	protected int port;

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

	/**
	 * This method, implemented in {@link BBPacket} subclasses, updates the server based on the data in the packet.
	 * Only packets received by the server are handled.
	 */
	public abstract void handle();

	/**
	 * Compares two {@link BBPacket} objects and specifies which has a higher priority (determined based on the
	 * packets' {@link #ID} values. Packets with the highest priority are {@link #handle() handled} first during each
	 * {@link PacketManager} cycle.
	 * 
	 * @param anotherPacket The BBPacket to which to compare this packet
	 * @return A negative integer if this packet has a higher priority than the one it is being compared to, a positive
	 * integer if the other packet has the higher priority, and zero if the packets share the same priority level.
	 */
	public int compareTo(BBPacket anotherPacket) {
		return (ID / 10) - (anotherPacket.ID / 10);
	}

	/**
	 * @return The packet's {@link #ID}
	 */
	public int getID() {
		return ID;
	}

	/**
	 * @return The packet's {@link #data}
	 */
	public String getData() {
		return data;
	}

	/**
	 * @return The packet's {@link #address}
	 */
	public InetAddress getAddress() {
		return address;
	}

	/**
	 * Sets the packet's source/destination {@link #address} to the provided InetAddress
	 * 
	 * @param address
	 */
	public void setAddress(InetAddress address) {
		this.address = address;
	}

	/**
	 * @return The packet's {@link #port}
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Sets the packet's source/destination port to the provided number
	 * 
	 * @param port A positive integer in the range of ports
	 */
	public void setPort(int port) {
		this.port = port;
	}
}