package game.world;

import game.world.constraints.Edge;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

public class CollisionData {
    public BPoint vertex;
    public Edge edge;
    public ArrayRealVector overlap; //vertex to edge

    public CollisionData(BPoint vertex, @NotNull Edge edge, @NotNull ArrayRealVector overlap) {
        this.vertex = vertex;
        this.edge=edge;
        this.overlap = overlap.copy();
    }
    public BPoint getEdge1(){//wtf.... redundant.
        return edge.getEdge1();
    }
    public BPoint getEdge2(){
        return edge.getEdge2();
    }
    public Edge getEdge(){
        return edge;
    }

    public BPoint getVertex() {
        return vertex;
    }

    public ArrayRealVector getOverlap() {
        return overlap;
    }
}
