package game.connection.handlers;

import game.GameHandler;
import game.Player;
import game.connection.packets.messages.ClientMessage;
import game.connection.packets.wrappers.ClientPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Logger;

public class ServerPlayerHandler extends ChannelInboundHandlerAdapter {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    Player player;
    ByteBuf buffer;
    int length;


    public ServerPlayerHandler(Player player) {
        this.player = player;
        buffer = Unpooled.directBuffer(2048);
        length = 0;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof ByteBuf) {
            buffer.writeBytes((ByteBuf) msg);
            if (length == 0) {
                length = buffer.readInt();
            }
            if (buffer.readableBytes() >= length) {
                try {
                    player.receive(new ClientPacket(ClientMessage.parseFrom(new ByteBufInputStream(buffer, length))));
                } catch (IOException e) {
                    logger.warning("Failed to deserialize client packet");// this just happens sometimes? idk.
                } finally {
                    ByteBuf temp = Unpooled.directBuffer(buffer.readableBytes());
                    buffer.readBytes(temp);
                    buffer = temp;
                    length = 0;
                }
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
