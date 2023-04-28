package game.world;

import game.world.constraints.DistanceConstraint;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

public class CollisionData {
    public VPoint vertex;
    public DistanceConstraint edge;
    public ArrayRealVector overlap; //vertex to edge

    public CollisionData(VPoint vertex, @NotNull DistanceConstraint edge, @NotNull ArrayRealVector overlap) {
        this.vertex = vertex;
        this.edge=edge;
        this.overlap = overlap.copy();
    }
    public VPoint getEdge1(){
        return edge.getPoints().getKey();
    }
    public VPoint getEdge2(){
        return edge.getPoints().getValue();
    }
    public DistanceConstraint getEdge(){
        return edge;
    }

    public VPoint getVertex() {
        return vertex;
    }

    public ArrayRealVector getOverlap() {
        return overlap;
    }
}
