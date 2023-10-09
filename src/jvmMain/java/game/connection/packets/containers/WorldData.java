package game.connection.packets.containers;

import game.world.World;
import game.world.bodies.Body;
import game.connection.packets.containers.images.BodyImage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class WorldData {
    public ArrayList<BodyImage> bodyImages;
    public WorldData() {//unnecessary containers
        bodyImages =new ArrayList<>();
        for(Body body:World.getBodies()){
            bodyImages.add(new BodyImage(body));
        }
    }
}
