package game.connection.handlers;

import com.google.gson.Gson;
import game.connection.packets.ServerPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;

public class ClientDecoder extends ObjectDecoder {
    Gson gson;

    public ClientDecoder(int maxObjectSize, ClassResolver classResolver) {
        super(maxObjectSize, classResolver);
        gson = new Gson();
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object msg = super.decode(ctx, in);
        if(msg instanceof String||msg==null){
            return gson.fromJson((String) msg, ServerPacket.class);
        } else{
            return msg;
        }
    }
}
