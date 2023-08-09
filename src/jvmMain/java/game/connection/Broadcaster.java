package game.connection;

import game.Config;
import game.connection.packets.containers.ServerStatus;
import game.util.Multiplayer;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static game.util.Multiplayer.groupAddress;
import static game.util.Multiplayer.multicastPort;

public class Broadcaster {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static DatagramSocket socket = null;
    private static final ServerStatus serverStatus = new ServerStatus(Multiplayer.localAddress,0,"placeholder");

    static ScheduledFuture<?> future;

    public static void setStatus(String message, int tcpPort) {
        synchronized (serverStatus) {
            serverStatus.port = tcpPort;
            serverStatus.message = message;
            logger.info("Set server status as " + serverStatus.getStatus());
        }
    }

    public static void start() {
        {
            try {
                socket = new DatagramSocket();
            } catch (IOException e) {
                logger.warning("Failed to create a broadcaster socket");
            }
        }// initialize socket
        future = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            byte[] buffer = serverStatus.getStatus().getBytes();// do I need this synchronised?
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, groupAddress, multicastPort);
            try {
                socket.send(packet);
            } catch (IOException e) {
                logger.warning("Failed to broadcast server status: " + e.getMessage());
            }
        }, 0, Config.multicastMilliPeriod, TimeUnit.MILLISECONDS);// start broadcast
        String unit = (Config.multicastMilliPeriod == 1000) ? " second" : " seconds";
        logger.info("Screaming at " + groupAddress.getHostAddress() + ":" + multicastPort + " every " + Config.multicastMilliPeriod / 1000 + unit);
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
