package game.connection;

import game.GameHandler;
import game.GameState;
import game.connection.handlers.ServerDecoder;
import game.connection.handlers.ServerPlayerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.logging.Logger;


public class ServerTCP extends Thread {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    ChannelGroup tcpChannels;
    Channel ch;
    int tcpPort;

    public ServerTCP(int tcpPort) {
        this.tcpPort = tcpPort;
        tcpChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    public synchronized void run() {
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
                    //.option(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline pipe = ch.pipeline();
                            pipe.addLast(
                                    new ServerPlayerHandler(GameHandler.connectPlayer(ch))
                            );
                            logger.info("New connection with " + ch.remoteAddress().getAddress() + ":" + ch.remoteAddress().getPort());
                        }

                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            tcpChannels.add(ctx.channel());
                            super.channelActive(ctx);
                        }
                    });
            // Bind and start to accept incoming connections.
            try {
                ch = b.bind(tcpPort).addListener(future -> logger.info("TCP server on at " + Addresses.localAddress.getHostAddress() + ":" + tcpPort)).channel();
                ch.closeFuture().sync();
            } catch (InterruptedException e) {
                logger.severe(e.getMessage());
            }
        } finally {
            if (GameHandler.getState() == GameState.host) {
                GameHandler.hostToMain();
            } else if (GameHandler.getState() == GameState.playServer) {
                GameHandler.playServerToHost();
                GameHandler.hostToMain();
            }
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
