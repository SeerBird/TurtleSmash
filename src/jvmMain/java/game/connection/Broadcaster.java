package game.connection;

import game.connection.packets.wrappers.containers.ServerStatus;
import game.util.DevConfig;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static game.connection.Addresses.groupAddress;
import static game.connection.Addresses.multicastPort;

public class Broadcaster {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static DatagramSocket socket = null;
    private static final ServerStatus serverStatus = new ServerStatus(Addresses.localAddress, 0, "placeholder");

    static ScheduledFuture<?> future;

    public static void setPort(int tcpPort) {
        synchronized (serverStatus) {
            serverStatus.port = tcpPort;
            logger.info("Set server status to " + serverStatus.getStatus());
        }
    }

    public static void setMessage(String message) {
        synchronized (serverStatus) {
            serverStatus.message = message;
            logger.info("Set server status to " + serverStatus.getStatus());
        }
    }

    public static void start() {
        //region initialize socket
        try {
            socket = new DatagramSocket();
        } catch (IOException e) {
            logger.warning("Failed to create a broadcaster socket");
        }
        //endregion
        future = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            byte[] buffer = serverStatus.getStatus().getBytes(); // do I need this synchronised?
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, groupAddress, multicastPort);
            try {
                socket.send(packet);
            } catch (IOException e) {
                logger.warning("Failed to broadcast server status: " + e.getMessage());
            }
        }, 0, DevConfig.multicastMilliPeriod, TimeUnit.MILLISECONDS);// start broadcast
        String unit = (DevConfig.multicastMilliPeriod == 1000) ? " second" : " seconds";
        logger.info("Screaming at " + groupAddress.getHostAddress() + ":" + multicastPort + " every " + DevConfig.multicastMilliPeriod / 1000 + unit);
    }

    public static void stop() {
        if (future != null) {
            future.cancel(false);
        }
        if (socket != null) {
            socket.close();
        }
    }
}
