package game;

import com.google.gson.Gson;
import game.connection.packets.ClientPacket;
import game.input.InputInfo;
import game.connection.packets.ServerPacket;
import game.world.bodies.Body;
import game.world.bodies.Box;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

public class Player {
    Box body; //should be turtle
    EventManager handler;
    InputInfo input;
    SocketChannel channel;
    Gson gson;

    public Player(@NotNull EventManager handler) {
        this.handler = handler;
        gson = new Gson();
        input = new InputInfo();
        body = new Box(handler.getWorld(), handler.getMousepos(), new ArrayRealVector(new Double[]{40.0, 0.0}), new ArrayRealVector(new Double[]{0.0, 40.0}));
    }

    public void setBody(Box body) {
        this.body = body;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public void setChannel(SocketChannel channel) {
        this.channel = channel;
    }

    public InputInfo getInput() {
        return input;
    }

    //Player Actions
    public void flingWeb(@NotNull ArrayRealVector target) {
        input.webFling = target.copy();
    }

    public void receive(@NotNull ClientPacket packet) {
        //unpack it here? seems harmless
        input = packet.getInput();
    }

    public void send(ServerPacket packet) {
        if (channel != null) {
            String json= gson.toJson(packet);
            channel.writeAndFlush(json).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(!future.isSuccess()){
                        try {
                            throw future.cause();
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        }
    }

    public Body getBody() {
        return body;
    }
}
