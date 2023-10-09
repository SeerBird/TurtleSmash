package game.connection.handlers;

import game.Player;
import game.connection.packets.ClientPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.logging.Logger;

public class ServerPlayerHandler extends ChannelInboundHandlerAdapter {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    Player player;

    public ServerPlayerHandler(Player player) {
        this.player = player;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // Echo back the received object to the client.
        if (msg instanceof ClientPacket) {
            player.receive((ClientPacket) msg);
        }else{
            logger.warning("Unknown message type");
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

}
