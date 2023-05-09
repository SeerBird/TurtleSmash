package game.connection;

import game.connection.handlers.ClientUDPHandler;
import game.connection.packets.data.ServerStatus;
import game.util.Multiplayer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.BindException;
import java.net.InetAddress;
import java.util.*;
import java.util.logging.Logger;

public class ClientUDP extends Thread {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    Map<InetAddress, ServerStatus> servers;

    public ClientUDP() {
        servers = Collections.synchronizedMap(new HashMap<>());
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
                Channel ch = b.bind(Multiplayer.UDPPort).sync().channel();
                ch.closeFuture().sync();
            } catch (Exception e) {
                if (e instanceof BindException) {// the compiler is a gleecking liar
                    logger.warning(e.getMessage());
                } else {
                    throw new RuntimeException(e);
                }
            }
        } finally {
            udpGroup.shutdownGracefully();
        }
    }

    public ArrayList<ServerStatus> getServers() {
        return new ArrayList<>(servers.values());
    }
}
