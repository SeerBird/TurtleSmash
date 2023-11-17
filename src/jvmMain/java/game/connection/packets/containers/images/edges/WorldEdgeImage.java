package game.connection.packets.containers.images.edges;

import game.world.World;
import game.world.bodies.Body;
import game.world.constraints.Edge;
import game.world.constraints.FixedEdge;
import org.jetbrains.annotations.NotNull;

public class WorldEdgeImage implements EdgeImage{
    public int bodyindex1;
    public int bodyindex2;
    public int index1;
    public int index2;
    public double distance;

    public WorldEdgeImage(@NotNull Edge e) {
        Body parent = e.getEdge1().getParentBody();
        bodyindex1 = World.getBodies().indexOf(parent);
        index1 = parent.getPoints().indexOf(e.getEdge1());
        parent = e.getEdge1().getParentBody();
        bodyindex1 = World.getBodies().indexOf(parent);
        index2 = parent.getPoints().indexOf(e.getEdge2());
        distance = e.getRestDistance();
    }

    public Edge getEdge() {
        return new FixedEdge(World.getBodies().get(bodyindex1).getPoints().get(index1), World.getBodies().get(bodyindex2).getPoints().get(index2), distance);
    }
}
