package game.connection.handlers;

import game.GameHandler;
import game.GameState;
import game.connection.packets.ServerPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Logger;

public class ClientTcpHandler extends ChannelInboundHandlerAdapter {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    //long last = 0;

    public ClientTcpHandler() {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            //logger.warning("Time since last receive: "+(System.nanoTime()-last)/1000000.0/16.666666 + " frames");
            //last = System.nanoTime();
            try {
                GameHandler.receiveServerPacket((ServerPacket)
                        new ObjectInputStream(new ByteBufInputStream((ByteBuf) msg)).readObject());
            } catch (IOException e) {
                logger.warning("Failed to deserialize server packet: " + e);// this just happens sometimes? idk.
            } catch (ClassNotFoundException e) {
                logger.warning("Unknown message type");
            }
        } else {
            logger.severe("Message isn't a ByteBuf, what??");
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        if (GameHandler.getState() == GameState.lobby) {
            GameHandler.lobbyToDiscover();
        } else if (GameHandler.getState() == GameState.playClient) {
            GameHandler.playClientToDiscover();
        }
        ctx.disconnect();
        ctx.close();
    }
}
