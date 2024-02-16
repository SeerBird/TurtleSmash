package game.connection.handlers;

import game.GameHandler;
import game.Player;
import game.connection.packets.wrappers.ClientPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Logger;

public class ServerPlayerHandler extends ChannelInboundHandlerAdapter {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    Player player;

    public ServerPlayerHandler(Player player) {
        this.player = player;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof ByteBuf) {
            try {
                player.receive((ClientPacket)
                        new ObjectInputStream(new ByteBufInputStream((ByteBuf) msg)).readObject());
            } catch (IOException e) {
                logger.warning("Failed to deserialize client packet");// this just happens sometimes? idk.
            } catch (ClassNotFoundException e) {
                logger.warning("Unknown message type");
            }
        } else {
            logger.warning("Message isn't a ByteBuf, what??");
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        GameHandler.removePlayer(player);
        logger.info("Player " + player.getChannel().remoteAddress() + " disconnected.");
    }
}
