package game.connection;

import game.Config;
import game.GameHandler;
import game.GameState;
import game.connection.handlers.ClientDecoder;
import game.connection.handlers.ClientTcpHandler;
import game.connection.handlers.ExceptionHandler;
import game.connection.packets.ClientPacket;
import game.connection.packets.containers.ServerStatus;
import game.input.InputInfo;
import game.connection.gson.gsonRegistry;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.logging.Logger;

public class ClientTCP extends Thread {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    Channel channel;
    final ServerStatus target;

    public ClientTCP(ServerStatus target) {
        this.target = target;
    }

    public void run() {
        logger.info("Trying to connect to " + target.address.getHostAddress() + ":" + target.port);
        EventLoopGroup group = new NioEventLoopGroup();
        Runtime.getRuntime().addShutdownHook(new Thread(group::shutdownGracefully));
        /*
        final SslContext sslCtx;
        try {
            sslCtx = Multiplayer.buildSslContext();
        } catch (CertificateException | SSLException e) {
            throw new RuntimeException(e);
        }
         */
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            /*
                            if (sslCtx != null) {
                                p.addLast(sslCtx.newHandler(ch.alloc(), "localhost", 5445));
                            }*/
                            p.addLast(
                                    new ObjectEncoder(),
                                    new ClientDecoder(1048576, ClassResolvers.cacheDisabled(null)),
                                    new ClientTcpHandler()
                            );
                        }
                    });
            ChannelFuture connectFuture = b.connect(target.address, target.port).addListener(future -> {
                if (future.isSuccess()) {
                    logger.info("TCP client connected");
                } else {
                    logger.warning("Failed to connect to server: " + future.cause().getMessage());
                    if (GameHandler.getState() == GameState.lobby) {
                        GameHandler.lobbyToDiscover();
                    } else if (GameHandler.getState() == GameState.playClient) {
                        GameHandler.playClientToDiscover();
                    }
                }
            });
            channel = connectFuture.channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            logger.severe(e.getMessage());
        } finally {
            group.shutdownGracefully().addListener(future -> logger.info("TCP client off"));
        }
    }

    public void send(InputInfo input) {
        if (channel != null) {
            channel.writeAndFlush(gsonRegistry.gson.toJson(new ClientPacket(input, GameHandler.getPlayers().get(0).getName())));
        }
    }

    public void disconnect() {
        if (channel != null) {
            channel.close().addListener(future -> logger.info("TCP channel closed"));
        }
    }
}
