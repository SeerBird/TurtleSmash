package game.connection.packets.wrappers.containers.images.edges;

import game.world.World;
import game.world.bodies.Body;
import game.world.constraints.Edge;
import game.world.constraints.FixedEdge;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;

public class WorldEdgeImage implements EdgeImage {
    @Serial
    private static final long serialVersionUID = 8008504;
    public int bi1;
    public int bi2;
    public int i1;
    public int i2;
    public float d;

    public WorldEdgeImage(@NotNull Edge e) {
        Body parent = e.getEdge1().getParentBody();
        bi1 = World.getBodies().indexOf(parent);
        i1 = parent.getPoints().indexOf(e.getEdge1());
        parent = e.getEdge2().getParentBody();
        bi2 = World.getBodies().indexOf(parent);
        i2 = parent.getPoints().indexOf(e.getEdge2());
        d = (float) e.getRestDistance();
    }

    public Edge getEdge() {
        if (bi1 == -1 || bi2 == -1 || i1 == -1 || i2 == -1) {
            return null;
        }
        return new FixedEdge(World.getBodies().get(bi1).getPoints().get(i1),
                World.getBodies().get(bi2).getPoints().get(i2), d);
    }
}
