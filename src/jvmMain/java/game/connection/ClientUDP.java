package game.connection;

import game.CONSTANTS;
import game.connection.handlers.ClientUDPHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

public class ClientUDP extends Thread {
    Map<String, Long> servers;

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
            // Bind and start to accept incoming connections.
            try {
                Channel ch = b.bind(CONSTANTS.UDP_PORT).sync().channel();
                ch.closeFuture().sync(); //remove sync?
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } finally {
            udpGroup.shutdownGracefully();
        }
    }

    public Set<String> getServers() {
        return servers.keySet();
    }
}
