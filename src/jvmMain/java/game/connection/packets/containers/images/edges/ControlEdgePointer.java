package game.connection.packets.containers.images.edges;

import game.world.BPoint;
import game.world.bodies.Body;
import game.world.constraints.Edge;
import game.world.constraints.FixedEdge;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ControlEdgePointer implements EdgeImage{
    public int index1;
    public int index2;
    public double distance;
    public ControlEdgePointer(@NotNull Edge e){
        ArrayList<BPoint> points=e.getEdge1().getParentBody().getPoints();
        index1=points.indexOf(e.getEdge1());
        index2=points.indexOf(e.getEdge2());
        distance=e.getRestDistance();
    }
}
