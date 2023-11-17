package game.connection.packets.containers.images.edges;

import game.world.BPoint;
import game.world.bodies.Body;
import game.world.constraints.Edge;
import game.world.constraints.FixedEdge;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FixedEdgeImage implements EdgeImage{
    public int index1;
    public int index2;
    public double distance;
    public FixedEdgeImage(@NotNull Edge e){
        ArrayList<BPoint> points=e.getEdge1().getParentBody().getPoints();
        index1=points.indexOf(e.getEdge1());
        index2=points.indexOf(e.getEdge2());
        distance=e.getRestDistance();
    }
    public Edge getEdge(@NotNull Body body){
        return new FixedEdge(body.getPoints().get(index1),body.getPoints().get(index2),distance);
    }
}
