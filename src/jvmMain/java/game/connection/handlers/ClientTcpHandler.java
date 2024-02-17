package game.connection.handlers;

import com.google.protobuf.CodedInputStream;
import game.GameHandler;
import game.GameState;
import game.connection.packets.messages.ServerMessage;
import game.connection.packets.wrappers.ServerPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Logger;

public class ClientTcpHandler extends ChannelInboundHandlerAdapter {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static ByteBuf buffer = Unpooled.directBuffer(2048);
    private static int length = 0;
    //long last = 0;

    public ClientTcpHandler() {
    }

    int dataSize = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            //logger.warning("Time since last receive: "+(System.nanoTime()-last)/1000000.0/16.666666 + " frames");
            //last = System.nanoTime();
            if (((ByteBuf) msg).readableBytes() != dataSize) {
                //logger.info(String.valueOf((dataSize = ((ByteBuf) msg).readableBytes())));
            }
            buffer.writeBytes((ByteBuf) msg);
            if (length == 0) {
                length = buffer.readInt();
            }
            if (buffer.readableBytes() >= length) {
                try {
                    GameHandler.receiveServerPacket(new ServerPacket(
                            ServerMessage.parseFrom(new ByteBufInputStream(buffer, length))));
                } catch (IOException e) {
                    logger.warning("Failed to deserialize server packet: " + e);// this just happens sometimes? idk.
                } finally {
                    ByteBuf temp = Unpooled.directBuffer(buffer.readableBytes());
                    buffer.readBytes(temp);
                    buffer = temp;
                    length = 0;
                }
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
