package com.veltro.blazingbarrels.server.connect;

import java.util.TreeSet;

import com.veltro.blazingbarrels.server.BBServer;
import com.veltro.blazingbarrels.server.connect.packet.BBPacket;

/**
 * The PacketManager is where all of the server's logic - which is based on the packets it receives - occurs. The
 * server's operation is split into 2-part "cycles", which execute every {@value #CYCLE_LENGTH} milliseconds.<p>
 * 
 * In the first phase of a cycle, the PacketManager iterates through the packets that have accumulated in the
 * {@link ReceiverThread}'s queue since the last cycle, casting them to {@link BBPacket} subclasses and updating the
 * game (moving players, handling weapon firing and collisions, etc.) based on their data.<p>
 * 
 * The second phase of each cycle is oriented towards updating the BlazingBarrels clients connected to the server. The
 * PacketManager determines which changes to the game should be sent to which clients and then generates the
 * appropriate BBPacket subclasses and adds them to the {@link SenderThread}'s outbound packet queue.
 * 
 * @author LinearLogic
 * @since 0.2.0
 */
public class PacketManager {

	/**
	 * The minimum amount of time between cycles, in milliseconds
	 */
	public static final int CYCLE_LENGTH = 50;

	/**
	 * Executes a cycle, advancing the game based on packets received since the last cycle and generating response
	 * packets to be sent to update clients connected to the server.
	 *  
	 * @see PacketManager Complete description of a cycle
	 */
	public void runCycle() {
		long startTime = System.currentTimeMillis();

		// Handle newly arrived packets:
		TreeSet<BBPacket> packets = new TreeSet<BBPacket>(); // A TreeSet is used to order packets by priority
		while (!BBServer.getReceiverDaemon().incomingPacketQueue.isEmpty())
			packets.add(BBServer.getReceiverDaemon().incomingPacketQueue.poll());
		for (BBPacket p : packets)
			p.handle();

		// TODO: Generate response packets

		// Round out the cycle length:
		int dt = (int) (System.currentTimeMillis() - startTime);
		if (dt < CYCLE_LENGTH) {
			synchronized (this) {
				try {
					this.wait(CYCLE_LENGTH - dt);
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.exit(0);
				}
			}
		}
	}
}
