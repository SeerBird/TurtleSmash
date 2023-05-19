package game.connection;

import game.EventManager;
import game.connection.handlers.ClientDecoder;
import game.connection.handlers.ClientTcpHandler;
import game.connection.handlers.ExceptionHandler;
import game.connection.packets.ClientPacket;
import game.connection.packets.containers.ServerStatus;
import game.input.InputInfo;
import game.util.Multiplayer;
import game.util.Util;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.ssl.SslContext;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;
import java.util.logging.Logger;

public class ClientTCP extends Thread {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    EventManager handler;
    Channel channel;
    ServerStatus target;

    public ClientTCP(EventManager handler, ServerStatus target) {
        this.handler = handler;
        this.target = target;
    }

    public void run() {
        logger.info("Trying to connect to " + target.address.getHostAddress() + ":" + target.port);
        EventLoopGroup group = new NioEventLoopGroup();
        Runtime.getRuntime().addShutdownHook(new Thread(group::shutdownGracefully));
        final SslContext sslCtx;
        try {
            sslCtx = Multiplayer.buildSslContext();
        } catch (CertificateException | SSLException e) {
            throw new RuntimeException(e);
        }
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            if (sslCtx != null) {
                                p.addLast(sslCtx.newHandler(ch.alloc(), Multiplayer.localhost, 5445));
                            }
                            p.addLast(
                                    new ObjectEncoder(),
                                    new ClientDecoder(1048576, ClassResolvers.cacheDisabled(null)),
                                    new ClientTcpHandler(handler),
                                    new ExceptionHandler(handler)
                            );
                        }
                    });
            try {
                ChannelFuture connectFuture = b.connect(target.address, target.port).addListener(future -> {
                    if (future.isSuccess()) {
                        logger.info("TCP client connected");
                    }
                });
                channel = connectFuture.sync().channel();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            handler.playToDiscover();
            disconnect();
            group.shutdownGracefully().addListener(future -> logger.info("TCP client off"));
        }
    }

    public void send(InputInfo input) {
        if (channel != null) {
            channel.writeAndFlush(Util.gson.toJson(new ClientPacket(input)));
        }
    }

    public void disconnect() {
        channel.close().addListener(future -> logger.info("TCP channel closed"));
    }
}
