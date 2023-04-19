package seerbird.game.world.bodies;

import javafx.util.Pair;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import seerbird.game.Config;
import seerbird.game.world.CollisionData;
import seerbird.game.world.VPoint;
import seerbird.game.world.World;
import seerbird.game.world.constraints.DistanceConstraint;

import java.awt.*;
import java.util.ArrayList;

public class Web extends Body {
    VPoint source;
    boolean attached;
    boolean isGrowing;
    ArrayList<Boolean> stuckEdges;
    ArrayList<Boolean> stuckPoints;

    // last point is attached to source
    public Web(@NotNull World world, @NotNull VPoint source, ArrayRealVector velocity) {
        super(world);
        stuckEdges = new ArrayList<>();
        stuckPoints = new ArrayList<>();
        gravitates = false;
        this.source = source;
        addPoint(new VPoint(this, 1, source.getPos()));
        points.get(0).accelerate(velocity);
        attached = true;
        isGrowing = true;
        pointColor = Color.red;
        edgeColor = Color.orange;
    }

    @Override
    public ArrayList<DistanceConstraint> getSides() {// do not return sides that are stuck
        ArrayList<DistanceConstraint> sides = new ArrayList<>();
        for (int i = 0; i < edges.size(); i++) {
            if (!stuckEdges.get(i)) {
                sides.add(edges.get(i));
            }
        }
        return sides;
    }

    @Override
    public void addEdge(VPoint p1, VPoint p2) {
        super.addEdge(p1, p2);
        stuckEdges.add(false);
    }

    @Override
    public void addPoint(VPoint p) {
        super.addPoint(p);
        stuckPoints.add(false);
    }

    @Override
    public void move() {
        super.move();
        if (attached) {
            if (isGrowing) {
                if (points.size() >= Config.stringLengthLimit) {
                    fix();
                }// stop growth when limit is reached
                else {
                    ArrayRealVector root = points.get(points.size() - 1).getPos();
                    ArrayRealVector dist = points.get(points.size() - 1).getDistance(source);
                    double distance = dist.getNorm() / Config.stringRestNodeDistance; // not in pixels but in rest distances
                    if (distance > 1) {
                        dist.mapMultiplyToSelf(1 / distance); // get a rest distance in the right direction
                        for (int i = 1; i < distance; i++) {
                            addPoint(new VPoint(this, 1, root.combineToSelf(1, 1, dist)));
                            addEdge(points.get(points.size() - 2), points.get(points.size() - 1));
                        }
                    }
                }// grow
            } else {
                // what does it do when attached but not growing? just moves? maybe collapse ifs
            }
        }// still attached
        else {
            // eh idk. space for content ig
        }
    }

    @Override
    public ArrayList<Pair<Double, VPoint>> project(@NotNull ArrayRealVector axis) {
        double norm = axis.getNorm();
        if (norm != 1.0) {
            axis.mapMultiplyToSelf(1 / norm);
        }
        VPoint minp = points.get(0);
        VPoint maxp = points.get(0);
        double min = axis.dotProduct(minp.getPos());
        double max = min;
        double projection;
        VPoint p;
        for (int i = 0; i < points.size(); i++) {//doing the first point over, idc
            if (!stuckPoints.get(i)) {
                p = points.get(i);
                projection = axis.dotProduct(p.getPos());
                if (projection > max) {
                    max = projection;
                    maxp = p;
                } else if (projection < min) {
                    min = projection;
                    minp = p;
                }
            }
        }
        ArrayList<Pair<Double, VPoint>> res = new ArrayList<>(); // I should probably change this to a pair
        res.add(new Pair<>(min, minp));
        res.add(new Pair<>(max, maxp));
        return res;
    }

    @Override
    public void collide(@NotNull CollisionData collision) {
        if (collision.getVertex().getParent() == this) {
            ArrayRealVector edge = collision.getEdge1().getDistance(collision.getEdge2());
            double edgeX = collision.getEdge2().getX() - collision.getEdge1().getX();
            double edgeY = collision.getEdge2().getY() - collision.getEdge1().getY();
            // should be 0 to 1, indicating where between edge1 and edge2 the vertex projection is
            double placement = (edge.getNorm() > 0) ? (Math.abs(edgeX) >= Math.abs(edgeY)) ? (collision.getVertex().getX() - collision.getEdge1().getX()) / (edgeX) : (collision.getVertex().getY() - collision.getEdge1().getY()) / (edgeY) : 0.5;
            collision.getVertex().setPos(collision.getEdge1().getPos().combine(1,1,edge.mapMultiply(placement)));
            addEdge(collision.getEdge1(), collision.getVertex());
            stuckEdges.set(stuckEdges.size() - 1, true);
            addEdge(collision.getEdge1(), collision.getVertex());
            stuckEdges.set(stuckEdges.size() - 1, true);
        }
    }

    public boolean isGrowing() {
        return isGrowing;
    }

    public void fix() {
        if (isGrowing) {
            isGrowing = false;
            addEdge(points.get(points.size() - 1), source);
            stuckEdges.set(stuckEdges.size() - 1, true);
        }
    }

    public void keepGrowing() {
        if (!isGrowing) {
            isGrowing = true;
            edges.remove(edges.size() - 1);
        }
    }

    public void detach() {
        if (attached) {
            if (!isGrowing) {
                edges.remove(edges.size() - 1);
            }
            attached = false;
        }
    }
}
