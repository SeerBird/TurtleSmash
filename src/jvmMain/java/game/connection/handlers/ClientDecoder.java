package game.connection.handlers;

import com.google.gson.JsonIOException;
import game.connection.packets.ServerPacket;
import game.connection.gson.gsonRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;

import java.util.logging.Logger;

public class ClientDecoder extends ObjectDecoder {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    public ClientDecoder(int maxObjectSize, ClassResolver classResolver) {
        super(maxObjectSize, classResolver);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object msg = super.decode(ctx, in);
        if (msg instanceof String) {
            try {
                String clazz = ((String) msg).substring(0, ((String) msg).indexOf("{"));
                msg = ((String) msg).substring(clazz.length());
                if (clazz.equals(ServerPacket.class.toString())) {
                    return gsonRegistry.gson.fromJson((String) msg, ServerPacket.class);
                }else{
                    logger.severe("Unknown message type");
                    return null;
                }
            } catch (JsonIOException e) {
                logger.severe(e.getMessage());
                return null;
            }
        } else if (msg == null) { // I don't really get why I did this. This won't happen either way. or at least it shouldn't
            return gsonRegistry.gson.fromJson("", ServerPacket.class);
        } else {
            return msg;
        }
    }
}
