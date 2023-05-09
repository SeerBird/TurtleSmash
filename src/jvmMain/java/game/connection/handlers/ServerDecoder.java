package game.connection.handlers;

import com.google.gson.Gson;
import game.connection.packets.ClientPacket;
import game.connection.packets.ServerPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;

public class ServerDecoder extends ObjectDecoder {
    Gson gson;

    public ServerDecoder(int maxObjectSize, ClassResolver classResolver) {
        super(maxObjectSize, classResolver);
        gson = new Gson();
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object msg = super.decode(ctx, in);
        if(msg instanceof String){
            return gson.fromJson((String) msg, ClientPacket.class);
        } else{
            return msg;
        }
    }
}

