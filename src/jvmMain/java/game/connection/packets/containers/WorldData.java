package game.connection.packets.containers;

import game.world.World;
import game.world.bodies.Body;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class WorldData {
    public ArrayList<Body> bodies;
    public ArrayList<ArrayList<Pair<Pair<Integer,Integer>,Double>>> edgeImages;
    public WorldData(@NotNull World world) {//unnecessary containers
        bodies=new ArrayList<>();
        edgeImages=new ArrayList<>();
        ArrayList<Body> worldbodies;
        for(int i = 0; i< (worldbodies=world.getBodies()).size(); i++){
            bodies.add(worldbodies.get(i));
            edgeImages.add(worldbodies.get(i).getEdgesImage());
        }
    }
    public WorldData(){
    }
}
