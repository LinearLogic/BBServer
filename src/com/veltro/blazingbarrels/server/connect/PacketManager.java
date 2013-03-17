package com.veltro.blazingbarrels.server.connect;

import java.util.HashMap;
import java.util.TreeSet;

import com.veltro.blazingbarrels.server.BBServer;
import com.veltro.blazingbarrels.server.connect.packet.BBPacket;
import com.veltro.blazingbarrels.server.connect.packet.Packet10ServerSnapshot;
import com.veltro.blazingbarrels.server.connect.packet.Packet22PlayerDisconnect;
import com.veltro.blazingbarrels.server.connect.packet.Packet30PlayerUpdate;
import com.veltro.blazingbarrels.server.game.ChangeType;
import com.veltro.blazingbarrels.server.game.Player;
import com.veltro.blazingbarrels.server.game.World;

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
	 * The number of cycles that have elapsed since the last server snapshot was sent to all connected clients. This
	 * snapshot is sent every ten seconds, or 200 cycles; at the same time, this number is reset to 0.
	 */
	private int cycleCount = 0;

	/**
	 * A registry of all the currently running {@link DeauthTask} objects. Each task is coupled with the name of the
	 * player the task is running for.
	 */
	private HashMap<String, DeauthTask> deauthTasks = new HashMap<String, DeauthTask>();

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

		// Generate response packets:
		for (Player player : World.getPlayers()) {
			if (player.getChanges().length == 0) // This player does not need to be updated
				continue;
			ChangeType[] changes = player.getChanges();
			switch(changes[0]) {
			case DISCONNECT_KICK:
				broadcastPacket(new Packet22PlayerDisconnect(player.getName(), 2, null, 0));
				World.removePlayer(player);
				continue;
			case DISCONNECT_TIMEOUT:
				broadcastPacket(new Packet22PlayerDisconnect(player.getName(), 1, null, 0));
				World.removePlayer(player);
				continue;
			case DISCONNECT_QUIT:
				broadcastPacket(new Packet22PlayerDisconnect(player.getName(), 0, null, 0));
				World.removePlayer(player);
				continue;
			default:
				break;
			}
			if (cycleCount >= 200) { // Send a server snapshot instead of update packets
				for (Player p : World.getPlayers())
					sendServerSnapshot(p);
				continue;
			}
			Packet30PlayerUpdate outgoing = new Packet30PlayerUpdate(player.getName());
			for (ChangeType type : changes) {
				switch(type) {
				case ADMIN:
					outgoing.toggleAdminStatus();
					break;
				case GODMODE:
					outgoing.toggleGodMode();
					break;
				case HEALTH:
					outgoing.setHealth(player.getHealth());
					break;
				case LOCATION:
					outgoing.setLocation(player.getLocation());
					break;
				case VISIBILITY:
					outgoing.toggleVisibility();
					break;
				default:
					// TODO: log an error, as this should never be reached
					break;
				}
			}
			outgoing.updateData();
			broadcastPacket(outgoing);
		}

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
		if (++cycleCount > 200) {
			cycleCount = 0;
		}
	}

	/**
	 * Sends the provided packet to the client corresponding to the provided player
	 * 
	 * @param target A {@link Player} on the server (contained in the {@link World#players} list). This player is used to
	 * retrieve the IP address and port of the client to which the packet should be sent.
	 * @param packet A {@link BBPacket} subclass. The packet does not need to have its address or port specified.
	 */
	public void sendPacket(Player target, BBPacket packet) {
		packet.setAddress(target.getClientAddress());
		packet.setPort(target.getClientPort());
		BBServer.getSenderDaemon().outgoingPacketQueue.add(packet);
	}

	/**
	 * Sends the provided packet to all the clients with players on the server.
	 * 
	 * @param packet A {@link BBPacket} subclass. The packet does not need to have its address or port specified
	 */
	public void broadcastPacket(BBPacket packet) {
		for (Player p : World.getPlayers()) {
			packet.setAddress(p.getClientAddress());
			packet.setPort(p.getClientPort());
			BBServer.getSenderDaemon().outgoingPacketQueue.add(packet);
		}
	}

	/**
	 * Sends a snapshot of the server (transfered using one or more {@link Packet10ServerSnapshot} packets) to the
	 * client associated with the provided player.
	 * 
	 * @param target The player to send the server snapshot to
	 */
	public void sendServerSnapshot(Player target) {
		Packet10ServerSnapshot outgoing = new Packet10ServerSnapshot(true, target.getClientAddress(),
				target.getClientPort());
		for (Player p : World.getPlayers()) {
			if (outgoing.addPlayerSnapshot(p))
				continue;
			BBServer.getSenderDaemon().outgoingPacketQueue.add(outgoing);
			outgoing = new Packet10ServerSnapshot(false, target.getClientAddress(), target.getClientPort());
			outgoing.addPlayerSnapshot(p);
		}
	}

	/**
	 * Runs the provided task and registers it in the HashMap of {@link #deauthTasks}
	 */
	public synchronized void runDeauthTask(DeauthTask task) {
		if (deauthTasks.containsKey(task.getPlayerName())) // There is already a deauth task running for the this player
			return;
		deauthTasks.put(task.getPlayerName(), task.startTask());
	}

	/**
	 * Attempts to cancel the {@link DeauthTask} (if any) associated with the player with the provided name
	 * 
	 * @param playerName The name of the player whose client is being pinged with deauthorization warnings
	 */
	public synchronized void cancelDeauthTask(String playerName) {
		try {
			deauthTasks.get(playerName).interrupt();
		} catch (NullPointerException e) { // There isn't a deauth task running for the player with the provided name
			return;
		}
	}

	/**
	 * Determines whether a {@link DeauthTask} is running for the player with the provided name
	 * 
	 * @param playerName The name of the player to check
	 * @return 'true' iff a deauth task is running for that player
	 */
	public synchronized boolean hasAssociatedDeauthTask(String playerName) {
		return deauthTasks.containsKey(playerName);
	}
}
