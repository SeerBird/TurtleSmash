package game.connection;

import game.GameHandler;
import game.connection.handlers.ExceptionHandler;
import game.connection.handlers.ServerDecoder;
import game.connection.handlers.ServerPlayerHandler;
import game.util.Multiplayer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.util.concurrent.GlobalEventExecutor;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;
import java.util.logging.Logger;


public class ServerTCP extends Thread {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    ChannelGroup tcpChannels;
    Channel ch;
    int tcpPort;

    public ServerTCP(int tcpPort) {
        this.tcpPort=tcpPort;
        tcpChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
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
                                    new ServerPlayerHandler(GameHandler.connectPlayer(ch)),
                                    new ExceptionHandler()
                            );
                            logger.info("New connection with "+ch.remoteAddress().getAddress()+":"+ch.remoteAddress().getPort());
                        }
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            tcpChannels.add(ctx.channel());
                            super.channelActive(ctx);
                        }
                    });
            // Bind and start to accept incoming connections.
            try {
                ch = b.bind(tcpPort).addListener(future -> logger.info("TCP server on at "+Multiplayer.localAddress.getHostAddress()+":"+tcpPort)).sync().channel();
                ch.closeFuture().sync();
            } catch (InterruptedException e) {
                logger.severe(e.getMessage());
            }
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully().addListener(future -> logger.info("TCP server off"));
        }
    }

    public void disconnect() {
        if (ch != null) {
            tcpChannels.close();
            ch.close().addListener(future -> logger.info("Disconnected TCP server"));
        }
    }
}
