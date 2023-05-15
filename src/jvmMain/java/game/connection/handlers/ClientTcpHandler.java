package game.connection.handlers;

import game.EventManager;
import game.connection.packets.GameStartPacket;
import game.connection.packets.ServerPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.logging.Logger;

public class ClientTcpHandler extends ChannelInboundHandlerAdapter {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final EventManager handler;

    public ClientTcpHandler(EventManager handler){
        this.handler=handler;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof ServerPacket){
            handler.receiveServerPacket((ServerPacket)msg);
        }else if (msg instanceof GameStartPacket){
            handler.playClient();
        }else{
            logger.warning("Unknown message type received by client");
        }
        super.channelRead(ctx, msg);
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ctx.disconnect();
        ctx.close();
        logger.info("Channel inactive");
    }
}
