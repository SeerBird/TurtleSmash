package game;

import game.connection.packets.ClientPacket;
import game.connection.packets.Packet;
import game.input.InputInfo;
import game.util.Maths;
import game.util.Util;
import game.world.bodies.Body;
import game.world.bodies.Box;
import game.world.bodies.Turtle;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.SocketChannel;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class Player {
    Turtle body; //should be turtle
    GameHandler handler;
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    InputInfo input;
    SocketChannel channel;

    public Player(@NotNull GameHandler handler) {
        this.handler = handler;
        input = new InputInfo();
        body = new Turtle(handler.getWorld(), handler.getMousepos(),
                (ArrayRealVector) Maths.i.mapMultiply(20), (ArrayRealVector) Maths.j.mapMultiply(20));
    }

    public void setBody(Turtle body) {
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

    public void receive(@NotNull ClientPacket packet) {
        input = packet.getInput();
    }

    public void send(Packet packet) {
        if (channel != null) {
            String json = packet.getClass().toString() + Util.gson.toJson(packet);
            channel.writeAndFlush(json).addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    logger.warning(future.cause().getMessage());
                }
            });
        }
    }

    public Turtle getBody() {
        return body;
    }
}
