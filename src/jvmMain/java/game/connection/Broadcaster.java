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

public class Broadcaster {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static DatagramSocket socket = null;
    private final ServerStatus serverStatus = new ServerStatus();
    ScheduledFuture future;

    public Broadcaster(String message, int tcpPort) {
        {
            this.serverStatus.address = Multiplayer.localAddress;
            this.serverStatus.port = tcpPort;
            this.serverStatus.message = message;
        }// set server status
        {
            try {
                socket = new DatagramSocket();
                SocketAddress group = new InetSocketAddress(groupAddress, 4445);
            } catch (IOException e) {
                logger.warning("Failed to create a broadcaster socket");
            }
        }// initialize socket
        future = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            byte[] buffer = serverStatus.getStatus().getBytes();// do I need this synchronised?
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, groupAddress, 4445);
            try {
                socket.send(packet);
            } catch (IOException e) {
                logger.warning("Failed to broadcast server status");
            }
        }, 0, Config.multicastMilliPeriod, TimeUnit.MILLISECONDS);// start broadcast
        String unit = (Config.multicastMilliPeriod == 1000) ? " second" : " seconds";
        logger.info("Screaming at " + groupAddress.getHostAddress() + ":" + 4445 + " every " + Config.multicastMilliPeriod / 1000 + unit);
    }

    public void setStatus(String message, int tcpPort) {
        synchronized (this.serverStatus) {
            this.serverStatus.port = tcpPort;
            this.serverStatus.message = message;
            logger.info("Set server status as " + this.serverStatus);
        }
    }

    public void stop() {
        if (future != null) {
            future.cancel(true);
        }
        if (socket != null) {
            socket.close();
        }
    }
}
