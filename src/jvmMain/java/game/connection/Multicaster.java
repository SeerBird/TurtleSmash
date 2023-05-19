package game.connection;

import game.Config;
import game.connection.handlers.ClientUDPHandler;
import game.connection.packets.containers.ServerStatus;
import game.util.Multiplayer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Multicaster extends Thread {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final Map<InetAddress, ServerStatus> servers;
    private final InetSocketAddress groupAddress;
    ScheduledFuture<EventLoopGroup> broadcastSchedule;
    NioDatagramChannel ch;
    String serverStatus;
    EventLoopGroup group;
    boolean isServer;

    public Multicaster(String groupIP, boolean isServer,Map<InetAddress, ServerStatus> servers) {
        this.servers=servers;
        this.isServer = isServer;
        this.groupAddress = new InetSocketAddress(groupIP, Multiplayer.UDPPort);
        serverStatus = "";
    }

    public void run() {
        group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap()
                    .group(group)
                    .channelFactory((ChannelFactory<NioDatagramChannel>) () -> new NioDatagramChannel(InternetProtocolFamily.IPv4))
                    .localAddress(groupAddress.getAddress(), groupAddress.getPort())
                    .option(ChannelOption.SO_BROADCAST, true)
                    .option(ChannelOption.IP_MULTICAST_IF, Multiplayer.networkInterface)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.SO_RCVBUF, 2048)
                    .option(ChannelOption.IP_MULTICAST_TTL, 255)
                    .handler(new ClientUDPHandler(servers));
            ch = (NioDatagramChannel) b.bind(Multiplayer.UDPPort).sync().channel();
            ch.joinGroup(groupAddress, Multiplayer.networkInterface).addListener(future -> {
                logger.info("Listening for servers on " + groupAddress.getAddress().getHostAddress() + ':' + groupAddress.getPort()
                        + " on interface " + Multiplayer.networkInterface.getDisplayName());
            }).sync();
            if (isServer) {
                startBroadcast();
            }
            ch.closeFuture().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    //Server functionality

    public void broadcastToLan() {
        ch.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(serverStatus, CharsetUtil.UTF_8), groupAddress));
    }

    public void startBroadcast() {
        if (group != null) {
            long period = Config.multicastMilliPeriod;
            broadcastSchedule = (ScheduledFuture<EventLoopGroup>) group.scheduleAtFixedRate(() -> this.broadcastToLan(), period, period, TimeUnit.MILLISECONDS);
            String unit = (period == 1000) ? " second" : " seconds";
            logger.info("Screaming at " + groupAddress.getAddress().getHostAddress() + ":" + groupAddress.getPort() + " every " + Config.multicastMilliPeriod / 1000 + unit);
        }
    }

    public void stopBroadcast() {
        if (broadcastSchedule != null) {
            broadcastSchedule.cancel(false);
        }
    }

    public void setServerStatus(String serverStatus, int tcpPort) {
        this.serverStatus = Multiplayer.localIp.getHostAddress() + "/" + tcpPort + "/" + serverStatus;
        logger.info("Set server status as " + this.serverStatus);
    }

    //Client functionality

    public Map<InetAddress, ServerStatus> getServers() {
        HashMap<InetAddress, ServerStatus> activeServers = new HashMap<>();
        for (ServerStatus status : servers.values()) {
            if (System.nanoTime() - status.nanoTime < Config.discoveryMilliTimeout * 1000000) {
                activeServers.put(status.address, status);
            }
        }
        servers.clear();
        servers.putAll(activeServers);
        return servers;
    }

    public void disconnect() {
        stopBroadcast();
        if (ch != null) {
            ch.close().addListener(future -> logger.info("Multicaster down"));
        }
    }
}
