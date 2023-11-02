package game;

import game.connection.packets.ClientPacket;
import game.connection.packets.Packet;
import game.input.InputControl;
import game.input.InputInfo;
import game.output.audio.Audio;
import game.output.audio.Sound;
import game.util.Maths;
import game.util.Util;
import game.world.World;
import game.world.bodies.Body;
import game.world.bodies.Box;
import game.world.bodies.Turtle;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.SocketChannel;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.logging.Logger;

public class Player {
    Turtle body;
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    int score;
    public int deathTimer;
    InputInfo input;
    SocketChannel channel;

    public Player() {
        score = 0;
        deathTimer = 0;
        input = new InputInfo();
        World.playerSpawn(this);
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

    public void connectInput(@NotNull InputInfo input) {
        this.input = input;
    }

    public void die() {
        score++;
        setBody(null);
        GameHandler.killPlayer(this);
        Audio.playSound(Sound.death);
    }
}
