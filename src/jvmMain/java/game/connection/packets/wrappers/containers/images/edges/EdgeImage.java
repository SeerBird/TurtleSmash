package game.connection.packets.wrappers.containers.images.edges;

import game.connection.packets.messages.EdgeM;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

public abstract class EdgeImage implements Serializable {
    public abstract EdgeM getMessage();

    @Nullable
    public static EdgeImage getImageFromMessage(@NotNull EdgeM message) {
        if (message.hasFep()) {
            return new FixedEdgeImage(message.getFep());
        } else if (message.hasCep()) {
            return new ControlEdgePointer(message.getCep());
        } else if (message.hasBep()) {
            return new BodyEdgePointer(message.getBep());
        } else if (message.hasWei()) {
            return new WorldEdgeImage(message.getWei());
        }
        return null;
    }
}
