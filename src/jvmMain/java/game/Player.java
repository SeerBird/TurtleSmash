package game;

import game.connection.packets.ClientPacket;
import game.connection.packets.Packet;
import game.input.InputInfo;
import game.util.DevConfig;
import game.world.bodies.Turtle;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.SocketChannel;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.logging.Logger;

import static game.connection.gson.gsonRegistry.gson;

public class Player {
    Turtle body;
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    int score;
    public int deathTimer;
    InputInfo input;
    SocketChannel channel;
    String name;

    public Player(String name) {
        score = 0;
        deathTimer = 0;
        input = new InputInfo();
        claimName(name, 0);
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
        claimName(packet.name, 0);
    }

    private void claimName(String desiredName, int index) {
        for (Player player : GameHandler.getPlayers()) {
            if (Objects.equals(player.getName(), desiredName + (index == 0 ? "" : index))) {
                if (player != this) {
                    claimName(desiredName, index + 1);
                    return;
                }
            }
        }
        name = desiredName + (index == 0 ? "" : index);
    }

    public void send(Packet packet) {
        if (channel != null) {
            if (channel.isActive()) {
                String json = packet.getClass().toString() + gson.toJson(packet);
                channel.writeAndFlush(json).addListener((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        if (future.cause().getMessage() == null) {
                            logger.warning("Failed to send a packet to a player.");
                        } else {
                            logger.warning(future.cause().getMessage());
                        }
                    }
                });
            }
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
        deathTimer = DevConfig.deathFrames;
        GameHandler.killPlayer(this);
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }
}
