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
import java.util.logging.Logger;

public class Multicaster extends Thread {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final Map<InetAddress, ServerStatus> servers = new HashMap<>();
    private final InetSocketAddress groupAddress;
    NioDatagramChannel ch;
    String serverStatus;

    public Multicaster(String groupIP) {
        this.groupAddress = new InetSocketAddress(groupIP, Multiplayer.UDPPort);
        serverStatus = "";
    }

    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap()
                    .group(group)
                    .channelFactory((ChannelFactory<NioDatagramChannel>) () -> new NioDatagramChannel(InternetProtocolFamily.IPv4))
                    .localAddress(groupAddress.getAddress(), groupAddress.getPort())
                    .option(ChannelOption.SO_BROADCAST, true)
                    .option(ChannelOption.IP_MULTICAST_LOOP_DISABLED, false)
                    .option(ChannelOption.IP_MULTICAST_IF, Multiplayer.networkInterface)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        public void initChannel(NioDatagramChannel ch) throws Exception {
                            ch.pipeline().addLast(new ClientUDPHandler(servers));
                        }
                    });
            ch = (NioDatagramChannel) b.bind(Multiplayer.UDPPort).sync().channel();
            ch.joinGroup(groupAddress, Multiplayer.networkInterface).addListener(future -> {
                logger.info("Listening for servers on " + groupAddress.getAddress() + ':' + groupAddress.getPort()
                        + " on interface "+Multiplayer.networkInterface.getDisplayName());
            }).sync();
            ch.closeFuture().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    //Server functionality

    public void broadcastToLan() {
        if (ch != null) {
            ch.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(serverStatus, CharsetUtil.UTF_8),
                    groupAddress)).addListener(future -> {
                if (!future.isSuccess()) {
                    logger.warning(future.cause().getMessage());
                }
            });

        }
    }

    public void setServerStatus(String serverStatus) {
        this.serverStatus = Multiplayer.localIp + "/" + Multiplayer.TCPPort + "/" + serverStatus;
        logger.info("Set server status as " + this.serverStatus);
    }

    //Client functionality

    public Map<InetAddress, ServerStatus> getServers() {
        HashMap<InetAddress, ServerStatus> activeServers = new HashMap<>();
        for (ServerStatus status : servers.values()) {
            if (System.nanoTime() - status.nanoTime < Config.discoveryMilliTimeout * 1000) {
                activeServers.put(status.address, status);
            }
        }
        servers.clear();
        servers.putAll(activeServers);
        return servers;
    }

    public void disconnect() {
        if (ch != null) {
            ch.close().addListener(future -> logger.info("Multicaster down"));
        }
    }
}
