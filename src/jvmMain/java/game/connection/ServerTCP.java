package game.connection;

import game.EventManager;
import game.connection.handlers.ExceptionHandler;
import game.connection.handlers.GObjectDecoder;
import game.connection.handlers.GObjectEncoder;
import game.connection.handlers.ServerPlayerHandler;
import game.util.Multiplayer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;


public class ServerTCP extends Thread {
    EventManager handler;

    public ServerTCP(EventManager handler) {
        this.handler = handler;
    }

    ChannelGroup tcpChannels;

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
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipe = ch.pipeline();
                            if (sslCtx != null) {
                                pipe.addLast(sslCtx.newHandler(ch.alloc()));
                            }
                            pipe.addLast(
                                    new ObjectEncoder(),
                                    new GObjectDecoder(1048576, ClassResolvers.cacheDisabled(null)),
                                    new ServerPlayerHandler(handler.addPlayer(ch)),
                                    new ExceptionHandler(handler)
                            );
                        }

                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            tcpChannels.add(ctx.channel());
                            super.channelActive(ctx);
                            ctx.write("hoohoo");
                        }
                    });

            // Bind and start to accept incoming connections.
            try {
                b.bind(Multiplayer.TCPPort).sync().channel().closeFuture().sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
