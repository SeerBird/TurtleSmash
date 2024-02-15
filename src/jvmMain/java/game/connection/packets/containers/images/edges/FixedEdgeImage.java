package game.connection.packets.containers.images.edges;

import game.world.BPoint;
import game.world.bodies.Body;
import game.world.constraints.Edge;
import game.world.constraints.FixedEdge;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.util.ArrayList;

public class FixedEdgeImage implements EdgeImage{
    @Serial
    private static final long serialVersionUID = 8008503;
    public int i1;
    public int i2;
    public float d;
    public FixedEdgeImage(@NotNull Edge e){
        ArrayList<BPoint> points=e.getEdge1().getParentBody().getPoints();
        i1 =points.indexOf(e.getEdge1());
        i2 =points.indexOf(e.getEdge2());
        d = (float) e.getRestDistance();
    }
    public Edge getEdge(@NotNull Body body){
        return new FixedEdge(body.getPoints().get(i1),body.getPoints().get(i2), d);
    }
}
