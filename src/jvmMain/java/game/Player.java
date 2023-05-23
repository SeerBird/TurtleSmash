package game;

import com.google.gson.Gson;
import game.connection.packets.ClientPacket;
import game.connection.packets.Packet;
import game.input.InputInfo;
import game.connection.packets.ServerPacket;
import game.util.Util;
import game.world.bodies.Body;
import game.world.bodies.Box;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.SocketChannel;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class Player {
    Box body; //should be turtle
    EventManager handler;
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    InputInfo input;
    SocketChannel channel;

    public Player(@NotNull EventManager handler) {
        this.handler = handler;
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
        input.teleport = target.copy();
    }

    public void receive(@NotNull ClientPacket packet) {
        input = packet.getInput();
    }

    public void send(Packet packet) {
        if (channel != null) {
            String json= Util.gson.toJson(packet);
            channel.writeAndFlush(json).addListener((ChannelFutureListener) future -> {
                if(!future.isSuccess()){
                    logger.warning(future.cause().getMessage());
                }
            });
        }
    }

    public Body getBody() {
        return body;
    }
}
