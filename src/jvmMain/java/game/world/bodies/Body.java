package game.world.bodies;

import game.world.CollisionData;
import game.world.VPoint;
import game.world.World;
import game.world.constraints.DistanceConstraint;
import javafx.util.Pair;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Body {
    ArrayList<VPoint> points;
    ArrayList<DistanceConstraint> edges;
    transient World parentWorld;
    ArrayRealVector movement;
    ArrayRealVector acceleration;
    double relevance;
    static double defaultRelevance = 5;
    boolean gravitates;
    double mass;
    ArrayRealVector center;
    boolean centerMoved;

    public Body(@NotNull World world) {
        points = new ArrayList<>();
        edges = new ArrayList<>();
        this.parentWorld = world;
        acceleration = new ArrayRealVector(2);
        movement = new ArrayRealVector(2);
        center = new ArrayRealVector(2);
        world.addBody(this); // might be unnecessary here, could be done outside
        relevance = 20;
        //edgeColor = Color.getHSBColor((float) Math.random(), 1, 1);
        //pointColor = Color.getHSBColor((float) Math.random(), 1, 1);
        gravitates = true;
        mass = 0;
        centerMoved = true;
    }

    public void move() {
        for (VPoint p : points) {
            p.accelerate(acceleration);
            p.move(movement);
        }//maybe merging the loops is okay
        movement.set(0);
        acceleration.set(0);
        //move
        for (VPoint p : points) {
            p.move();
        }
        centerMoved = true;
    }

    public void shift(ArrayRealVector v) {
        movement.combineToSelf(1, 1, v);
        acceleration.combineToSelf(1, -1, v);
    }

    public void move(ArrayRealVector v) {
        movement.combineToSelf(1, 1, v);
    }

    public void accelerate(RealVector v) {
        acceleration.combineToSelf(1, 1, v);
    }

    public boolean constrain() {
        boolean satisfied = false;
        for (DistanceConstraint c : edges) {
            satisfied |= c.satisfy();
        }
        return satisfied;
    }

    public World getParentWorld() {
        return parentWorld;
    }

    public double getRelevance() {
        return relevance;
    }

    public double getMass() {
        return mass;
    }

    public ArrayRealVector getCenter() {
        if (centerMoved) {
            mass = 0;
            center.set(0);
            for (VPoint p : points) {
                mass += p.getMass();
                center.combineToSelf(1, p.getMass(), p.getPos());
            }
            center.mapMultiplyToSelf(1 / mass);
            centerMoved = false;
        }
        return center.copy();
    }

    public void refreshMass() {
        mass = 0;
        center.set(0);
        for (VPoint p : points) {
            mass += p.getMass();
            center.combineToSelf(1, p.getMass(), p.getPos());
        }
        center.mapMultiplyToSelf(1 / mass);
        centerMoved = false;
    }

    public void addPoint(VPoint p) {
        points.add(p);
        centerMoved = true;
    }

    public void addEdge(VPoint p1, VPoint p2) {
        addEdge(new DistanceConstraint(p1, p2, p1.getDistance(p2).getNorm()));
    }

    public ArrayRealVector getDistance(@NotNull Body b) {
        return parentWorld.getDistance(getCenter(), b.getCenter());
    }

    public ArrayRealVector getDistance(@NotNull ArrayRealVector p) {
        return parentWorld.getDistance(getCenter(), p);
    }

    public boolean gravitates() {
        return gravitates;
    }

    public ArrayList<VPoint> getPoints() {
        return points;
    }

    public ArrayList<DistanceConstraint> getSides() { // remember to override in webs not to collide with stuck parts
        return edges;
    }

    final public ArrayList<DistanceConstraint> getEdges() {
        return edges;
    }

    public void addEdge(DistanceConstraint e) {
        edges.add(e);
    }
    public void setParent(World parent){
        this.parentWorld =parent;
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

    public void stop() {
        for (VPoint p : points) {
            p.stop();
        }
    }

    public void delete() {
        parentWorld.deleteBody(this);
    }

    public void decreaseRelevance(double decrease) {
        relevance -= decrease;
    }

    public void resetRelevance() {
        relevance = defaultRelevance;
    }

    public void collide(@NotNull CollisionData collision) { //I can play with elasticity here - accelerate vs move
        ArrayRealVector overlap = collision.getOverlap().copy(); //possibly unnecessary copy and therefore declaration
        ArrayRealVector edge = collision.getEdge1().getDistance(collision.getEdge2());
        double edgeX = collision.getEdge2().getX() - collision.getEdge1().getX();
        double edgeY = collision.getEdge2().getY() - collision.getEdge1().getY();
        // should be 0 to 1, indicating where between edge1 and edge2 the vertex projection is
        double placement = (edge.getNorm() > 0) ? (Math.abs(edgeX) >= Math.abs(edgeY)) ? (collision.getVertex().getX() - collision.getEdge1().getX()) / (edgeX) : (collision.getVertex().getY() - collision.getEdge1().getY()) / (edgeY) : 0.5;
        double scaleFactor = 1 / (Math.pow(placement, 2) + Math.pow(1 - placement, 2)); // normalising factor
        // I like to move it, move it
        double elasticity = 1;
        collision.getVertex().accelerate(overlap.mapMultiply(0.25 * elasticity));
        collision.getEdge1().accelerate(overlap.mapMultiply(-0.25 * scaleFactor * (1 - placement) * elasticity));
        collision.getEdge2().accelerate(overlap.mapMultiply(-0.25 * scaleFactor * placement * elasticity));
        elasticity = 1 - elasticity;
        if (elasticity != 0) {
            collision.getVertex().move(overlap.mapMultiply(0.25 * elasticity));
            collision.getEdge1().move(overlap.mapMultiply(-0.25 * scaleFactor * (1 - placement) * elasticity));
            collision.getEdge2().move(overlap.mapMultiply(-0.25 * scaleFactor * placement * elasticity));
        }
    }

    public void checkPointParent() {
        for(VPoint p:points){
            p.setParentBody(this);
        }
        for(DistanceConstraint e:edges){
            e.getEdge2().setParentBody(this);
            e.getEdge1().setParentBody(this);
        }
    }
/*
    public Body copy(World parent) {
        Body b = new Body(parent);

    }

    private void addMesh(ArrayList<VPoint> p, ArrayList<DistanceConstraint> e, VPoint start, Body parent) {
        if (!p.contains(start)) {
            for (DistanceConstraint edge : edges) {
                if (edge.getEdge1() == start) {
                    VPoint copy = start.copy(parent);

                } else if (edge.getEdge2() == start) {

                }
            }
        }
    }
 */
}
