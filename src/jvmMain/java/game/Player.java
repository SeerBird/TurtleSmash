package game;

import game.connection.packets.messages.ServerMessage;
import game.connection.packets.wrappers.ClientPacket;
import game.connection.packets.wrappers.ServerPacket;
import game.input.InputInfo;
import game.output.ui.TurtleMenu;
import game.util.DevConfig;
import game.world.bodies.Turtle;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.SocketChannel;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Objects;
import java.util.logging.Logger;

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
        input.detachWeb |= packet.getInput().detachWeb;
        input.webFling |= packet.getInput().webFling;
        input.create |= packet.getInput().create;
        input.teleport |= packet.getInput().teleport;
        input.mousepos = packet.getInput().mousepos;
        if (!Objects.equals(packet.name, name)) {
            claimName(packet.name);
        }
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

    int dataSize = 0;

    public void send(ServerPacket packet) {
        if (channel != null) {
            if (channel.isActive()) {
                try {
                    ServerMessage data = packet.getMessage();
                    ByteBuf msg = Unpooled.directBuffer(data.getSerializedSize() + 4);
                    msg.writeInt(data.getSerializedSize());
                    data.writeTo(new ByteBufOutputStream(msg));
                    if (msg.readableBytes() != dataSize) {
                        logger.info(String.valueOf(dataSize = msg.readableBytes()));
                    }
                    channel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
                        if (!future.isSuccess()) {
                            if (future.cause().getMessage() == null) {
                                logger.warning("Failed to send a packet to a player.");
                            } else {
                                logger.warning(future.cause().getMessage());
                            }
                        }
                    });
                } catch (IndexOutOfBoundsException e) {
                    logger.severe("Too many things going on! Can't send this!");
                } catch (IOException e) {
                    logger.severe("I/O exception serializing server packet: " + e);
                }
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
