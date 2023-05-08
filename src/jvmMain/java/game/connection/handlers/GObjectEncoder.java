package game.connection.handlers;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.Serializable;

public class GObjectEncoder extends ObjectEncoder {
    Gson gson;

    public GObjectEncoder() {
        gson = new Gson();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Serializable msg, ByteBuf out) throws Exception {
        String jsonMsg = gson.toJson(msg);
        super.encode(ctx, jsonMsg, out);
    }
}
