package game.connection.handlers;

import game.connection.packets.GameStartPacket;
import game.connection.packets.ServerPacket;
import game.util.Util;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;

public class ClientDecoder extends ObjectDecoder {
    public ClientDecoder(int maxObjectSize, ClassResolver classResolver) {
        super(maxObjectSize, classResolver);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object msg = super.decode(ctx, in);
        if (msg instanceof String) {
            String clazz = ((String) msg).substring(0, ((String) msg).indexOf("{"));
            msg = ((String) msg).substring(clazz.length());
            if (clazz.equals(ServerPacket.class.toString())) {
                return Util.gson.fromJson((String) msg, ServerPacket.class);
            } else if (clazz.equals(GameStartPacket.class.toString())) {
                return Util.gson.fromJson((String) msg, GameStartPacket.class);
            } else {
                return "Failed to decode server packet";
            }
        } else if (msg == null) { // I don't really get why I did this. This won't happen either way. or at least it shouldn't
            return Util.gson.fromJson("", ServerPacket.class);
        } else {
            return msg;
        }
    }
}
