package game.connection.handlers;

import game.Player;
import game.connection.packets.ClientPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerPlayerHandler extends ChannelInboundHandlerAdapter {
    Player player;

    public ServerPlayerHandler(Player player) {
        this.player = player;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // Echo back the received object to the client.
        if (msg instanceof ClientPacket) {
            player.receive((ClientPacket) msg);
        }
        //ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

}
