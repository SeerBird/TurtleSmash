package game.connection.packets.containers.images;

import game.world.VPoint;
import game.world.bodies.Body;
import game.world.constraints.Edge;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class EdgeImage {
    public int index1;
    public int index2;
    public double distance;
    public EdgeImage(@NotNull Edge e){
        ArrayList<VPoint> points=e.getEdge1().getParentBody().getPoints();
        index1=points.indexOf(e.getEdge1());
        index2=points.indexOf(e.getEdge2());
        distance=e.getDistance();
    }
    public Edge getEdge(@NotNull Body body){
        return new Edge(body.getPoints().get(index1),body.getPoints().get(index2),distance);
    }
}
