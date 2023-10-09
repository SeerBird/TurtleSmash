package game.connection.handlers;

import game.GameHandler;
import game.connection.packets.GameStartPacket;
import game.connection.packets.ServerPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.logging.Logger;

public class ClientTcpHandler extends ChannelInboundHandlerAdapter {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public ClientTcpHandler(){
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof ServerPacket){
            GameHandler.receiveServerPacket((ServerPacket)msg);
        }else if (msg instanceof GameStartPacket){
            GameHandler.playClient();
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
