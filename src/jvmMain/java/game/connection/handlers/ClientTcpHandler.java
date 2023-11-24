package game.connection.handlers;

import game.GameHandler;
import game.GameState;
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
        }else{
            logger.warning("Unknown message type");
        }
        super.channelRead(ctx, msg);
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        if(GameHandler.getState()== GameState.lobby){
            GameHandler.lobbyToDiscover();
        }else if(GameHandler.getState()== GameState.playClient){
            GameHandler.playClientToDiscover();
        }
        ctx.disconnect();
        ctx.close();
    }
}
