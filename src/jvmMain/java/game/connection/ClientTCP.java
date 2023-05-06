package game.connection;

import game.EventManager;
import game.connection.handlers.ClientTcpHandler;
import game.connection.packets.ClientPacket;
import game.input.InputInfo;
import game.util.Multiplayer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;
import java.net.InetAddress;
import java.util.ArrayList;

public class ClientTCP extends Thread {
    EventManager handler;
    ArrayList<InetAddress> LANServers;
    Channel channel;

    public ClientTCP(EventManager handler) {
        this.handler = handler;
        LANServers = new ArrayList<>();
    }

    public void start() {
        {
            final SslContext sslCtx;
            if (Multiplayer.SSL) {
                try {
                    sslCtx = SslContextBuilder.forClient()
                            .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
                } catch (SSLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                sslCtx = null;
            }
            EventLoopGroup group = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(group)
                        .channel(NioServerSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline p = ch.pipeline();
                                if (sslCtx != null) {
                                    p.addLast(sslCtx.newHandler(ch.alloc(), Multiplayer.localhost, Multiplayer.TCPPort));
                                }
                                p.addLast(
                                        new ObjectEncoder(),
                                        new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                        new ClientTcpHandler());
                            }
                        });
                // Bind and start to accept incoming connections.
                try {
                    channel = b.bind(Multiplayer.TCPPort).sync().channel();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                channel.closeFuture().sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                group.shutdownGracefully();
            }
        }// TCP
    }

    public void send(InputInfo input) {
        channel.writeAndFlush(new ClientPacket(input));
    }
}
