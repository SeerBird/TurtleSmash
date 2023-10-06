package game.connection.packets.containers.images;

import game.world.VPoint;
import game.world.World;
import game.world.bodies.Body;
import game.world.constraints.Edge;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class WorldEdgeImage {
    public int bodyindex1;
    public int bodyindex2;
    public int index1;
    public int index2;
    public double distance;

    public WorldEdgeImage(@NotNull Edge e) {
        Body parent = e.getEdge1().getParentBody();
        bodyindex1 = parent.getParentWorld().getBodies().indexOf(parent);
        index1 = parent.getPoints().indexOf(e.getEdge1());
        parent = e.getEdge1().getParentBody();
        bodyindex1 = parent.getParentWorld().getBodies().indexOf(parent);
        index2 = parent.getPoints().indexOf(e.getEdge2());
        distance = e.getDistance();
    }

    public Edge getEdge(@NotNull World world) {
        return new Edge(world.getBodies().get(bodyindex1).getPoints().get(index1), world.getBodies().get(bodyindex2).getPoints().get(index2), distance);
    }
}
