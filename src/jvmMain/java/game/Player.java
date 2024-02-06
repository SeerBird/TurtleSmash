package game;

import game.connection.packets.ClientPacket;
import game.connection.packets.ServerPacket;
import game.input.InputInfo;
import game.output.ui.TurtleMenu;
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
        deathTimer = DevConfig.deathFrames;
        input = new InputInfo();
        claimName(name);
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
        claimName(packet.name);
    }

    public void claimName(String desiredName) {
        if (checkUniqueName(desiredName)) {
            name = desiredName;
        } else {
            for (int i = 1; i < 1000; i++) {
                if (checkUniqueName(desiredName + i)) {
                    name = desiredName + i;
                    return;
                }
            }
            throw new RuntimeException("Too many players.");
        }
    }

    private boolean checkUniqueName(String name) {
        for (Player player : GameHandler.getPlayers()) {
            if (Objects.equals(player.getName(), name)) {
                if (player != this) {
                    return false;
                }
            }
        }
        return true;
    }

    public void send(ServerPacket packet) {
        if (channel != null) {
            if (channel.isActive()) {
                String json;
                try {
                    long time = System.nanoTime();
                    json = packet.getClass() + gson.toJson(packet);
                    logger.warning("Json length: "+json.length());
                    if ((time = System.nanoTime() - time) > 1204200 * 5) {
                        logger.warning("Took " + time/1000000 + " millis to serialize!");
                    }
                } catch (IllegalArgumentException e) {
                    logger.severe(e.getMessage());
                    return;
                } catch (Exception other) {
                    logger.severe(other.getMessage());
                    throw new RuntimeException(other.getMessage());
                }
                long time = System.nanoTime();
                channel.writeAndFlush(json).addListener((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        if (future.cause().getMessage() == null) {
                            logger.warning("Failed to send a packet to a player.");
                        } else {
                            logger.warning(future.cause().getMessage());
                        }
                    } else {
                        //logger.warning("Took " + (System.nanoTime() - time) / 1000000.0 + " millis to send packet");
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
        TurtleMenu.refreshScores();
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

}
