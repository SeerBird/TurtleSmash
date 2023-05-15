package game.connection;

import game.EventManager;
import game.connection.handlers.ExceptionHandler;
import game.connection.handlers.ClientDecoder;
import game.connection.handlers.ServerDecoder;
import game.connection.handlers.ServerPlayerHandler;
import game.util.Multiplayer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;


public class ServerTCP extends Thread {
    EventManager handler;
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    ChannelGroup tcpChannels;
    Channel ch;

    public ServerTCP(EventManager handler) {
        this.handler = handler;
        tcpChannels=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    public synchronized void run() {
        // Configure SSL.
        final SslContext sslCtx;
        try {
            sslCtx = Multiplayer.buildSslContext();
        } catch (CertificateException | SSLException e) {
            throw new RuntimeException(e);
        }
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }));
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new ChannelDuplexHandler())
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipe = ch.pipeline();
                            if (sslCtx != null) {
                                pipe.addLast(sslCtx.newHandler(ch.alloc()));
                            }
                            pipe.addLast(
                                    new ObjectEncoder(),
                                    new ServerDecoder(1048576, ClassResolvers.cacheDisabled(null)),
                                    new ServerPlayerHandler(handler.addPlayer(ch)),
                                    new ExceptionHandler(handler)
                            );
                        }

                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            tcpChannels.add(ctx.channel());
                            super.channelActive(ctx);
                        }
                    });

            // Bind and start to accept incoming connections.
            try {
                ch = b.bind(Multiplayer.TCPPort).addListener(future->logger.info("TCP server on")).sync().channel();
                ch.closeFuture().sync();
            } catch (InterruptedException e) {
                logger.severe(e.getMessage());
            }
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully().addListener(future->logger.info("TCP server off"));
        }
    }

    public void disconnect() {
        tcpChannels.close();
        ch.close().addListener(future->logger.info("Disconnected TCP server"));
    }
}
