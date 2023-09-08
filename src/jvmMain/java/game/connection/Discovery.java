package game.connection;

import game.connection.packets.containers.ServerStatus;

import java.io.IOException;
import java.net.*;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static game.util.Multiplayer.*;

public class Discovery {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    protected static DatagramSocket socket;
    static ScheduledFuture<?> future;
    protected static byte[] buf = new byte[256];
    private static final InetSocketAddress group = new InetSocketAddress(groupAddress, multicastPort);

    public static void start(Map<InetAddress, ServerStatus> servers) {
        try {
            socket = new MulticastSocket(multicastPort);
        } catch (IOException e) {
            logger.warning("Failed to create multicast discoverer socket"); // rethrow here
        }
        try {
            socket.joinGroup(group, networkInterface);
            //socket.setOption(StandardSocketOptions.IP_MULTICAST_LOOP,false);
        } catch (IOException e) {
            logger.warning("Failed to join multicast group"); // rethrow here
        }
        future = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            {
                try {
                    socket.receive(packet);
                } catch (IOException e) {
                    logger.warning("I/O exception receiving multicast packet");
                }
            } // receive packet
            ServerStatus received;
            {
                String status = new String(
                        packet.getData(), 0, packet.getLength());
                try {
                    received = new ServerStatus(status);
                } catch (UnknownHostException e) {
                    logger.warning("Failed to assign received server IP to an address");
                    return;
                } catch (IndexOutOfBoundsException e) {
                    logger.warning("Unknown format of received message");
                    return;
                }
            } // parse the packet as a ServerStatus
            servers.put(received.address, received); // add the discovered server to the server list
        }, 2000, 1, TimeUnit.MILLISECONDS);
        logger.info("Listening for servers on " + group.getAddress().getHostAddress() + ':' + multicastPort
                + " on interface " + networkInterface.getDisplayName());
    }

    public static void stop() {
        if (future != null) {
            future.cancel(true);
        }
        if (socket != null) {
            try {
                socket.leaveGroup(group, networkInterface);
            } catch (IOException e) {
                logger.warning("Error leaving group");
            }
            socket.close();
        }
        logger.info("No longer listening for servers");
    }

    public void shutDown() {
        if (future != null) {
            future.cancel(true);
        }
    }
}