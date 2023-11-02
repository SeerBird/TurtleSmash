package game.world.bodies;

import game.Config;
import game.world.BPoint;
import game.world.CollisionData;
import game.world.constraints.Edge;
import javafx.util.Pair;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class Web extends Body {
    boolean attached;
    @Nullable
    public BPoint source; //I don't like the fact that all of this is public
    public Edge target;
    public transient Edge sourceEdge; //those will handle themselves, dw
    public transient Edge targetEdge1;
    public transient Edge targetEdge2;
    boolean isGrowing;

    // points are ordered end to source
    public Web(@NotNull BPoint source, ArrayRealVector velocity) {
        super();
        this.source = source;
        addPoint(new BPoint(this, 1, source.getPos()));
        points.get(0).accelerate(velocity);
        isGrowing = true;
    }

    @Override
    public void move() {
        super.move();
        //region behavior when still attached
        if (source != null) {
            if (isGrowing) {
                //region add web points and edges so that there isn't a gap between the web and the source
                ArrayRealVector root = points.get(points.size() - 1).getPos();
                ArrayRealVector dist = points.get(points.size() - 1).getDistance(source);
                double distance = dist.getNorm() / Config.stringRestNodeDistance; // not in pixels but in rest distances
                if (distance > 1) {// if the gap is greater than a web edge rest distance
                    dist.mapMultiplyToSelf(1 / distance); // get the rest distance vector in the right direction
                    //region add points while there is a gap and the length limit has not been reached
                    for (int i = 1; i < distance; i++) {
                        if (points.size() >= Config.stringLengthLimit) {
                            sourceEdge = new Edge(source, points.get(points.size() - 1), Config.stringRestNodeDistance);
                            isGrowing = false;
                            break;
                        }
                        addPoint(new BPoint(this, 1, root.combineToSelf(1, 1, dist)));
                        addEdge(points.get(points.size() - 2), points.get(points.size() - 1));
                    }
                    //endregion
                }
                //endregion
            } else {
                // what does it do when attached but not growing? just moves? maybe collapse ifs
            }
        }
        //endregion
        //region behavior when disconnected from source turtle
        else {
            if (target == null) {

            }
            // eh idk. space for content ig
        }
        //endregion
    }

    @Override
    public void collide(@NotNull CollisionData collision) {
        //region get the two things that are sticking to one another
        target = collision.edge;
        BPoint sticky = collision.getVertex();
        //endregion
        //region move the sticky point onto its projection onto the target edge
        ArrayRealVector distance = target.getEdge1().getDistance(target.getEdge2());
        distance.mapMultiply(target.getDistance() / distance.getNorm());
        double edgeX = distance.getEntry(0);
        double edgeY = distance.getEntry(1);
        // should be 0 to 1, indicating where between edge1 and edge2 the vertex projection is
        double placement = (distance.getNorm() > 0) ? (Math.abs(edgeX) >= Math.abs(edgeY)) ? (sticky.getX() - target.getEdge1().getX()) / (edgeX) : (sticky.getY() - target.getEdge1().getY()) / (edgeY) : 0.5;
        sticky.setPos(target.getEdge1().getPos().combine(1, 1, distance.mapMultiply(placement)));
        //endregion
        //region create the two connections between the sticky point and the ends of the target edge
        targetEdge1 = new Edge(sticky, target.getEdge1(), distance.getNorm() * placement);
        targetEdge2 = new Edge(sticky, target.getEdge2(), distance.getNorm() * (1 - placement));
        //endregion
    }

    @Override
    public void gravitate(Body b) {
    }

    @Override
    public boolean constrain() {
        boolean sat = true;
        for (int i = 0; i < Config.webTensileStrength; i++) {
            sat &= super.constrain();

            if (sourceEdge != null) {
                sat &= sourceEdge.satisfy();
            }
            if (target != null) {
                sat &= targetEdge1.satisfy();
                sat &= targetEdge2.satisfy();
            }
        }
        return sat;
    }

    public BPoint getSticky() {
        return points.get(0);
    }

    public boolean isGrowing() {
        return isGrowing;
    }

    @Override
    public boolean gravitates() {
        return false;
    }

    public void keepGrowing() {
        if (!isGrowing) {
            isGrowing = true;
            edges.remove(edges.size() - 1);
        }
    }

    public boolean isSticky() {
        return target == null;
    }

    public Edge getTarget() {
        return target;
    }

    public Edge getSourceEdge() {
        return sourceEdge;
    }

    public void setGrowing(boolean isGrowing) {
        this.isGrowing = isGrowing;
    }

    public void disconnect() {
        sourceEdge = null;
        source = null;
    }

    @Override
    public boolean collides(@NotNull Body body) {
        return body.getClass() != Web.class&&isSticky();
    }
}
