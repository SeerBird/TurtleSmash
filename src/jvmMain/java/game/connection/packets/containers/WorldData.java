package game.connection.packets.containers;

import game.world.World;
import game.world.bodies.Body;
import game.connection.packets.containers.images.bodies.BodyImage;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class WorldData {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    public ArrayList<BodyImage> bodyImages;

    public WorldData() {//unnecessary containers(or so I thought at some point, why?)
        bodyImages = new ArrayList<>();
        for (Body body : World.getBodies()) {
            try {
                Constructor<? extends BodyImage> baba = BodyImage.getImageClass(body).getDeclaredConstructor(body.getClass());
                bodyImages.add(baba.newInstance(body));
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException ignored) {
                logger.severe("What the fuck!?!?!?");
            }
        }
    }
}
