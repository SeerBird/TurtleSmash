package game.connection.packets.containers;

import game.connection.packets.containers.images.bodies.BodyImage;
import game.world.bodies.Body;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class WorldData {
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
}
