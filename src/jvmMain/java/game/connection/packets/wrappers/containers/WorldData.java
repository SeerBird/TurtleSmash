package game.connection.packets.wrappers.containers;

import game.connection.packets.messages.ServerMessage;
import game.connection.packets.wrappers.containers.images.bodies.BodyImage;
import game.world.bodies.Body;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class WorldData implements Serializable {
    @Serial
    private static final long serialVersionUID = 800853;
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    public ArrayList<BodyImage<?>> bodyImages;

    public WorldData(ArrayList<Body> bodies) {
        bodyImages = new ArrayList<>();
        for (Body body : bodies) {
            try {
                Constructor<? extends BodyImage<?>> constructor = BodyImage.getImageClass(body).getDeclaredConstructor(body.getClass());
                bodyImages.add(constructor.newInstance(body));
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException |
                     NoSuchMethodException e) {
                logger.severe("What the fuck!?!?!?");
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    public WorldData(@NotNull ServerMessage.WorldM message) {
        bodyImages = new ArrayList<>();
        for (ServerMessage.WorldM.BodyM body : message.getBodyList()) {
            bodyImages.add(BodyImage.getImageFromMessage(body));
        }
    }

    public ServerMessage.WorldM getMessage() {
        ServerMessage.WorldM.Builder builder = ServerMessage.WorldM.newBuilder();
        for (BodyImage<?> body : bodyImages) {
            builder.addBody(body.getMessage());
        }
        return builder.build();
    }
}
