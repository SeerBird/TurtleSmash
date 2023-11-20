package game;

import game.connection.packets.ClientPacket;
import game.connection.packets.Packet;
import game.input.InputInfo;
import game.output.audio.Audio;
import game.output.audio.Sound;
import game.connection.gson.gsonRegistry;
import game.world.bodies.Turtle;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.SocketChannel;
import org.jetbrains.annotations.NotNull;

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
            String json = packet.getClass().toString() + gsonRegistry.gson.toJson(packet);
            channel.writeAndFlush(json).addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    if (future.cause().getMessage() == null) {
                        logger.warning("Can't send packet to a player.");
                    } else {
                        logger.warning(future.cause().getMessage());
                    }
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
