package game.connection.handlers;

import game.EventManager;
import game.connection.packets.ServerPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientTcpHandler extends ChannelInboundHandlerAdapter {
    private final EventManager handler;

    public ClientTcpHandler(EventManager handler){
        this.handler=handler;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof ServerPacket){
            handler.receive((ServerPacket)msg);
        }
        super.channelRead(ctx, msg);
    }
}
