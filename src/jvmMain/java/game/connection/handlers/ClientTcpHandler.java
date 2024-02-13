package game.connection.handlers;

import game.GameHandler;
import game.GameState;
import game.connection.packets.ServerPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.logging.Logger;

public class ClientTcpHandler extends ChannelInboundHandlerAdapter {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    long last = 0;

    public ClientTcpHandler(){
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof ServerPacket){
            logger.warning("Time since last receive: "+(System.nanoTime()-last)/1000000.0/16.666666 + " frames");
            last = System.nanoTime();
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
