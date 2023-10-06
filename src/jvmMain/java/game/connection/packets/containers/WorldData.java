package game.connection.packets.containers;

import game.world.World;
import game.world.bodies.Body;
import game.connection.packets.containers.images.BodyImage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class WorldData {
    public ArrayList<BodyImage> bodyImages;
    public WorldData(@NotNull World world) {//unnecessary containers
        bodyImages =new ArrayList<>();
        for(Body body:world.getBodies()){
            bodyImages.add(new BodyImage(body));
        }
    }
    public WorldData(){
    }
}
