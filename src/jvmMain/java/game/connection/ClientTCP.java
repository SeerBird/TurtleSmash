package game.connection;

import game.GameHandler;
import game.GameState;
import game.connection.handlers.ClientTcpHandler;
import game.connection.handlers.ExceptionHandler;
import game.connection.packets.wrappers.ClientPacket;
import game.connection.packets.wrappers.containers.ServerStatus;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;

public class ClientTCP extends Thread {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    Channel channel;
    final ServerStatus target;

    public ClientTCP(@NotNull ServerStatus target) {
        this.target = target;
    }

    public void run() {
        logger.info("Trying to connect to " + target.address.getHostAddress() + ":" + target.port);
        EventLoopGroup group = new NioEventLoopGroup();
        Runtime.getRuntime().addShutdownHook(new Thread(group::shutdownGracefully));
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(
                                    new ClientTcpHandler(),
                                    new ExceptionHandler()
                            );
                        }
                    });
            ChannelFuture connectFuture = b.connect(target.address, target.port).addListener(future -> {
                if (future.isSuccess()) {
                    logger.info("TCP client connected");
                } else {
                    logger.warning("Failed to connect to server: " + future.cause().getMessage());
                }
            });
            channel = connectFuture.channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) { //should not happen, ever
            logger.severe(e.getMessage());
        } finally {
            if (GameHandler.getState() == GameState.lobby) {
                GameHandler.lobbyToDiscover();
            } else if (GameHandler.getState() == GameState.playClient) {
                GameHandler.playClientToDiscover();
            }
            group.shutdownGracefully().addListener(future -> logger.info("TCP client off"));
        }
    }

    public void send(ClientPacket packet) {
        if (channel != null) {
            game.connection.packets.messages.ClientMessage data = packet.getMessage();
            ByteBuf msg = Unpooled.directBuffer(data.getSerializedSize()+4);
            msg.writeInt(data.getSerializedSize());
            msg.writeBytes(data.toByteArray());
            channel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    if (future.cause().getMessage() == null) {
                        logger.warning("Failed to send a packet to the server.");
                    } else {
                        logger.warning(future.cause().getMessage());
                    }
                }
            });
        }
    }

    public void disconnect() {
        if (channel != null) {
            channel.close().addListener(future -> logger.info("TCP channel closed"));
        }
    }
}
