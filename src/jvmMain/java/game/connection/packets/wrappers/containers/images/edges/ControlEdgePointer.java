package game.connection.packets.wrappers.containers.images.edges;

import game.world.BPoint;
import game.world.constraints.Edge;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.util.ArrayList;

public class ControlEdgePointer implements EdgeImage{
    @Serial
    private static final long serialVersionUID = 8008502;
    public int i1;
    public int i2;
    public ControlEdgePointer(@NotNull Edge e){
        ArrayList<BPoint> points=e.getEdge1().getParentBody().getPoints();
        i1 =points.indexOf(e.getEdge1());
        i2 =points.indexOf(e.getEdge2());
    }
}
