package game.connection;

import game.Config;
import game.connection.handlers.ClientUDPHandler;
import game.connection.packets.containers.ServerStatus;
import game.util.Multiplayer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.BindException;
import java.net.InetAddress;
import java.util.*;
import java.util.logging.Logger;

public class ClientUDP extends Thread {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final Map<InetAddress, ServerStatus> servers = new HashMap<>();
    Channel ch;

    public ClientUDP() {
    }

    @Override
    public void run() {
        EventLoopGroup udpGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(udpGroup)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ClientUDPHandler(servers));
            try {
                ch = b.bind(Multiplayer.UDPPort).addListener(future->logger.info("UDP listener on")).sync().channel();
                ch.closeFuture().sync();
            } catch (Exception e) {
                if (e instanceof BindException) {// the compiler is a gleecking liar
                    logger.warning(e.getMessage());
                } else {
                    throw new RuntimeException(e);
                }
            }
        } finally {
            udpGroup.shutdownGracefully().addListener(future->logger.info("UDP client off"));
        }
    }

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
            ch.close().addListener(future -> {
                if (future.isSuccess()) logger.info("UDP listener closed");
            });
        }
    }
}
