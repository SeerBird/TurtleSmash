package game.connection;

import game.util.Multiplayer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

public class ServerUDP extends Thread {
    Channel udpChannel;
    String serverStatus = "";

    public ServerUDP() {

    }

    @Override
    public void run() {
        EventLoopGroup udpGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(udpGroup)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new LoggingHandler(LogLevel.TRACE));
            // Bind and start to accept incoming connections.
            try {
                udpChannel = b.bind(0).sync().channel(); //remove sync?
                udpChannel.closeFuture().sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } finally {
            udpGroup.shutdownGracefully();
        }
    }

    public void broadcastToLan() {//maybe it doesn't interrupt? see tomorrow
        if (udpChannel != null) {
            udpChannel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(serverStatus, CharsetUtil.UTF_8),
                    new InetSocketAddress("255.255.255.255", Multiplayer.UDPPort)));

        }
    }

    public void setServerStatus(String serverStatus) {
        this.serverStatus = Multiplayer.localIp + "/" + Multiplayer.TCPPort + "/" + serverStatus;
    }

    public void disconnect() {
        if (udpChannel != null) {
            udpChannel.disconnect();
        }
    }
}
