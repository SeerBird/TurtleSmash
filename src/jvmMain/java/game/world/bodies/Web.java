package game.world.bodies;

import game.Config;
import game.world.BPoint;
import game.world.CollisionData;
import game.world.constraints.Edge;
import javafx.util.Pair;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Web extends Body {
    boolean attached;
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
        if (source != null) {
            if (isGrowing) {
                if (points.size() >= Config.stringLengthLimit) {
                    isGrowing=false;
                }// stop growth when limit is reached
                else {
                    ArrayRealVector root = points.get(points.size() - 1).getPos();
                    ArrayRealVector dist = points.get(points.size() - 1).getDistance(source);
                    double distance = dist.getNorm() / Config.stringRestNodeDistance; // not in pixels but in rest distances
                    if (distance > 1) {

                        dist.mapMultiplyToSelf(1 / distance); // get a rest distance in the right direction
                        for (int i = 1; i < distance; i++) {
                            addPoint(new BPoint(this, 1, root.combineToSelf(1, 1, dist)));
                            addEdge(points.get(points.size() - 2), points.get(points.size() - 1));
                        }
                    }// grow
                }
            } else {
                // what does it do when attached but not growing? just moves? maybe collapse ifs
            }
        }// still attached
        else {
            if (target == null) {

            }
            // eh idk. space for content ig
        }
    }

    @Override
    public ArrayList<Pair<Double, BPoint>> project(@NotNull ArrayRealVector axis) {
        double norm = axis.getNorm();
        if (norm != 1.0) {
            axis.mapMultiplyToSelf(1 / norm);
        }
        BPoint minp = points.get(0);
        BPoint maxp = points.get(0);
        double min = axis.dotProduct(minp.getPos());
        double max = min;
        double projection;
        BPoint p;
        for (BPoint point : points) {//doing the first point over, idc
            p = point;
            projection = axis.dotProduct(p.getPos());
            if (projection > max) {
                max = projection;
                maxp = p;
            } else if (projection < min) {
                min = projection;
                minp = p;
            }
        }
        ArrayList<Pair<Double, BPoint>> res = new ArrayList<>(); // I should probably change this to a pair. I hate pairs.
        res.add(new Pair<>(min, minp));
        res.add(new Pair<>(max, maxp));
        return res;
    }

    private void stick(@NotNull BPoint webPoint, @NotNull Edge edge) {//create a new one-sided distance constraint class?
    }

    @Override
    public void collide(@NotNull CollisionData collision) {
        target = collision.edge;
        BPoint sticky = collision.getVertex();
        ArrayRealVector distance = target.getEdge1().getDistance(target.getEdge2());
        distance.mapMultiply(target.getDistance() / distance.getNorm()); // make it work on the rest distance rather than the real distance
        double edgeX = distance.getEntry(0);
        double edgeY = distance.getEntry(1);
        // should be 0 to 1, indicating where between edge1 and edge2 the vertex projection is
        double placement = (distance.getNorm() > 0) ? (Math.abs(edgeX) >= Math.abs(edgeY)) ? (sticky.getX() - target.getEdge1().getX()) / (edgeX) : (sticky.getY() - target.getEdge1().getY()) / (edgeY) : 0.5;
        sticky.setPos(target.getEdge1().getPos().combine(1, 1, distance.mapMultiply(placement)));
        targetEdge1 = new Edge(sticky, target.getEdge1(), distance.getNorm() * placement);
        targetEdge2 = new Edge(sticky, target.getEdge2(), distance.getNorm() * (1 - placement));
    }

    @Override
    public void gravitate(Body b) {
    }

    @Override
    public boolean constrain() {
        boolean sat = super.constrain();
        if (!isGrowing) {
            if (sourceEdge == null) {
                sourceEdge = new Edge(source, points.get(points.size() - 1), Config.stringRestNodeDistance);
            }
            sat &= sourceEdge.satisfy();
        }
        if (target != null) {
            sat &= targetEdge1.satisfy();
            sat &= targetEdge2.satisfy();
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
        this.isGrowing=isGrowing;
    }
}
