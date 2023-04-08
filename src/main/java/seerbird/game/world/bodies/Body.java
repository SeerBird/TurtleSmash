package seerbird.game.world.bodies;

import javafx.util.Pair;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import seerbird.game.math.Compute;
import seerbird.game.world.CollisionData;
import seerbird.game.world.VPoint;
import seerbird.game.world.World;
import seerbird.game.world.constraints.Constraint;
import seerbird.game.world.constraints.DistanceConstraint;

import java.util.ArrayList;

public class Body implements Cloneable {
    ArrayList<VPoint> points;
    ArrayList<DistanceConstraint> edges;
    World world;
    ArrayRealVector shift;
    ArrayRealVector acceleration;

    public Body(World world) {
        points = new ArrayList<>();
        edges = new ArrayList<>();
        this.world = world;
    }

    public void addPoint(VPoint p) {
        points.add(p);
    }

    public void addEdge(DistanceConstraint e) {
        edges.add(e);
    }

    public void move() {
        //forces and stuff? probably outside
        for (VPoint p : points) {
            p.shift(shift);
            p.accelerate(acceleration);
        }
        shift.set(0);
        acceleration.set(0);
        //move
        for (VPoint p : points) {
            p.move();
        }
    }

    public void shift(ArrayRealVector v) {
        shift.combineToSelf(1, 1, v);
    }

    public void accelerate(ArrayRealVector v) {
        acceleration.combineToSelf(1, 1, v);
    }

    public boolean constrain() {
        boolean satisfied = false;
        for (Constraint c : edges) {
            satisfied |= c.satisfy();
        }
        return satisfied;
    }

    public World getWorld() {
        return world;
    }

    public ArrayList<VPoint> getPoints() {
        return points;
    }

    public ArrayList<DistanceConstraint> getEdges() { // remember to override in webs not to collide with stuck parts
        return edges;
    }

    public ArrayList<Pair<Double, VPoint>> project(@NotNull ArrayRealVector axis) {//returns minimum to maximum
        double norm = axis.getNorm();
        if (norm != 1.0) {
            axis.mapMultiplyToSelf(1 / norm);
        }
        VPoint minp = points.get(0);
        VPoint maxp = points.get(0);
        double min = axis.dotProduct(minp.getPos());
        double max = min;
        double projection;
        for (VPoint p : points) {//doing the first point over, idc
            projection = axis.dotProduct(p.getPos());
            if (projection > max) {
                max = projection;
                maxp = p;
            } else if (projection < min) {
                min = projection;
                minp = p;
            }
        }
        ArrayList<Pair<Double, VPoint>> res = new ArrayList<>(); // I should probably change this to a pair
        res.add(new Pair<>(min, minp));
        res.add(new Pair<>(max, maxp));
        return res;
    }

    public void collide(@NotNull CollisionData collision) { //disregards point mass
        ArrayRealVector overlap = collision.getOverlap().copy(); //possibly unnecessary copy and therefore declaration
        ArrayRealVector edge = collision.getEdge1().getDistance(collision.getEdge2());
        if (Compute.areClockwise(collision.getEdge1().getPos(), collision.getEdge2().getPos(), collision.getVertex().getPos())) {
            overlap.mapMultiplyToSelf(-1);
        }
        double edgeX = collision.getEdge2().getX() - collision.getEdge1().getX();
        double edgeY = collision.getEdge2().getY() - collision.getEdge1().getY();
        // should be 0 to 1, indicating where between edge1 and edge2 the vertex projection is
        double placement = (edge.getNorm() > 0) ? (Math.abs(edgeX) >= Math.abs(edgeY)) ? (collision.getVertex().getX() - collision.getEdge1().getX()) / (edgeX) : (collision.getVertex().getY() - collision.getEdge1().getY()) / (edgeY) : 0.5;
        if (placement < 0 || placement > 1) {
            boolean guck = true;
        } else {
            double scaleFactor = 1 / (Math.pow(placement, 2) + Math.pow(1 - placement, 2)); // normalising factor
            // I like to move it, move it
            collision.getVertex().accelerate(overlap.mapMultiply(0.5));
            collision.getEdge1().accelerate(overlap.mapMultiply(-0.5 * scaleFactor * (1 - placement)));
            collision.getEdge2().accelerate(overlap.mapMultiply(-0.5 * scaleFactor * placement));
        }
    }

    @Override
    public Body clone() {
        try {
            Body clone = (Body) super.clone();
            clone.points.clear();
            clone.edges.clear();
            clone.acceleration = acceleration.copy();
            clone.shift = shift.copy();
            for (VPoint p : points) {
                clone.points.add(p.clone());
            }
            for (DistanceConstraint e : edges) {
                clone.edges.add(e.clone());
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
