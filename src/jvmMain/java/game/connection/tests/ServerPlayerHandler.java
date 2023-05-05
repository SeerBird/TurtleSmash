package game.connection.tests;

import com.esotericsoftware.kryonet.Server;
import game.Player;
import game.connection.ClientPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Handles both client-side and server-side handler depending on which
 * constructor was called.
 */
public class ServerPlayerHandler extends ChannelInboundHandlerAdapter {
    Player player;
    public ServerPlayerHandler(Player player){
        this.player=player;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // Echo back the received object to the client.
        if(msg instanceof ClientPacket){
            player.receive((ClientPacket) msg);
        }
        //ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
