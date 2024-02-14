package game.world.bodies;

import game.GameHandler;
import game.connection.packets.containers.images.animations.CollisionBurstAnimationImage;
import game.connection.packets.containers.images.animations.ScreenShakeAnimationImage;
import game.output.Renderer;
import game.output.animations.CollisionBurstAnimation;
import game.output.animations.ScreenShakeAnimation;
import game.output.audio.Audio;
import game.output.audio.Sound;
import game.util.DevConfig;
import game.util.Maths;
import game.world.BPoint;
import game.world.CollisionData;
import game.world.World;
import game.world.constraints.ControlEdge;
import game.world.constraints.Edge;
import game.world.constraints.FixedEdge;
import javafx.util.Pair;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public abstract class Body {
    final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    ArrayList<BPoint> points;
    ArrayList<Edge> edges;
    ArrayRealVector movement;
    ArrayRealVector acceleration;
    double relevance;
    double mass;
    ArrayRealVector center;
    boolean centerMoved;
    ArrayRealVector velocity; // relies on getCenter being called every tick
    public ArrayList<Web> bound;

    public Body() {
        points = new ArrayList<>();
        edges = new ArrayList<>();
        bound = new ArrayList<>();
        acceleration = new ArrayRealVector(2);
        movement = new ArrayRealVector(2);
        center = new ArrayRealVector(2);
        velocity = new ArrayRealVector(2);
        relevance = 240;
        mass = 0;
        centerMoved = true;
    }

    public void move() {
        for (BPoint p : points) {
            p.accelerate(acceleration);
            p.move(movement);
        }//maybe merging the loops is okay
        movement.set(0);
        acceleration.set(0);
        //move
        for (BPoint p : points) {
            p.move();
        }
        centerMoved = true;
        getCenter();
        if (relevance < 0) {
            delete();
        }
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
        velocity.combineToSelf(1, 1, v);
    }

    public boolean constrain() {
        boolean satisfied = true;
        for (Edge c : edges) {
            satisfied &= c.satisfy();
        }
        return satisfied;
    }

    public double getMass() {
        return mass;
    }

    public ArrayRealVector getCenter() {
        if (centerMoved) {
            mass = 0;
            velocity = center.copy();
            center.set(0);
            for (BPoint p : points) {
                mass += p.getMass();
                center.combineToSelf(1, p.getMass(), p.getPos());
            }
            center.mapMultiplyToSelf(1 / mass);
            centerMoved = false;
            velocity.combineToSelf(-1, 1, center);
        }
        return center.copy();
    }

    public ArrayRealVector getVelocity() {
        getCenter();
        return velocity.copy();
    }

    void addPoint(BPoint p) {
        points.add(p);
        centerMoved = true;
    }

    public void addPoint(double mass, @NotNull ArrayRealVector pos) {
        addPoint(new BPoint(this, mass, pos));
    }

    public void addPoints(BPoint... points) {
        this.points.addAll(List.of(points));
        centerMoved = true;
    }

    public void addEdge(BPoint p1, BPoint p2) {
        addEdge(new FixedEdge(p1, p2, p1.getDistance(p2).getNorm()));
    }

    public void addEdge(BPoint p1, BPoint p2, MutableDouble control) {
        addEdge(new ControlEdge(p1, p2, control));
    }

    public void addEdgeChain(BPoint... points) {
        for (int i = 1; i < points.length; i++) {
            addEdge(points[i - 1], points[i]);
        }
    }

    public ArrayRealVector getDistance(@NotNull Body b) {
        return World.getDistance(getCenter(), b.getCenter());
    }

    public ArrayRealVector getDistance(@NotNull ArrayRealVector p) {
        return World.getDistance(getCenter(), p);
    }


    public ArrayList<BPoint> getPoints() {
        return points;
    }

    public ArrayList<Edge> getSides() { // remember to override in webs not to collide with stuck parts
        return edges;
    }

    final public ArrayList<Edge> getEdges() {
        return edges;
    }

    public void addEdge(Edge e) {
        edges.add(e);
    }

    public ArrayList<Pair<Double, BPoint>> project(@NotNull ArrayRealVector axis) {//returns minimum to maximum
        double norm = axis.getNorm();
        axis.mapMultiplyToSelf(1 / norm);//normalize
        BPoint minp = points.get(0);
        BPoint maxp = points.get(0);
        double min = axis.dotProduct(minp.getPos());
        double max = min;
        double projection;
        for (BPoint p : points) {//doing the first point over, idc
            projection = p.project(axis);
            if (projection > max) {
                max = projection;
                maxp = p;
            } else if (projection < min) {
                min = projection;
                minp = p;
            }
        }
        ArrayList<Pair<Double, BPoint>> res = new ArrayList<>(); // I should probably change this to a pair. I hate pairs
        res.add(new Pair<>(min, minp));
        res.add(new Pair<>(max, maxp));
        return res;
    }

    public void stop() {
        for (BPoint p : points) {
            p.stop();
        }
    }

    public boolean collides(Body body) {
        return true;
    }

    public void fade() {
        if (World.isOutOfBounds(this)) {
            relevance--;
        }
        if (relevance < 0) {
            delete();
        }
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
        double elasticity = DevConfig.elasticity;
        collision.getVertex().accelerate(overlap.mapMultiply(0.25 * elasticity));
        collision.getEdge1().accelerate(overlap.mapMultiply(-0.25 * scaleFactor * (1 - placement) * elasticity));
        collision.getEdge2().accelerate(overlap.mapMultiply(-0.25 * scaleFactor * placement * elasticity));
        elasticity = 1 - elasticity;
        if (elasticity != 0) {
            collision.getVertex().move(overlap.mapMultiply(0.25 * elasticity));
            collision.getEdge1().move(overlap.mapMultiply(-0.25 * scaleFactor * (1 - placement) * elasticity));
            collision.getEdge2().move(overlap.mapMultiply(-0.25 * scaleFactor * placement * elasticity));
        }
        GameHandler.broadcastAnimation(new ScreenShakeAnimationImage(
                (ScreenShakeAnimation) Renderer.addAnimation(new ScreenShakeAnimation(overlap.getNorm()))));
        GameHandler.broadcastAnimation(new CollisionBurstAnimationImage((CollisionBurstAnimation) Renderer.addAnimation(new CollisionBurstAnimation(collision))));
        long time = System.nanoTime();
        Audio.playCooldownSound(Sound.collision);
        if((time=(System.nanoTime()-time)/1000000)>10){
            logger.severe("Took "+time+" millis to out sound(outside)");
        }
    }

    static boolean intersect(@NotNull Edge edge1, @NotNull Edge edge2) {
        return Maths.intersect(edge1.getEdge1().getPos(), edge1.getEdge2().getPos(), edge2.getEdge1().getPos(), edge2.getEdge2().getPos());
    }

    public boolean gravitates() {
        return true;
    }

    public void gravitate(Body b) {
        ArrayRealVector force = getDistance(b); //equal to the distance vector for now, reusing vector objects
        double distance = Math.max(force.getNorm(), DevConfig.minGravityRadius);
        force.mapMultiplyToSelf(Math.pow(distance, -3) * DevConfig.gravity); // ~~r^-2
        accelerate(force.mapMultiply(b.getMass()));
        b.accelerate(force.mapMultiply(-getMass()));
    }

    public void delete() {
        World.removeBody(this);
        for (Web web : bound) {
            web.loseTarget();
        }
    }

    public void addBinder(Web web) {
        bound.add(web);
    }

    public void unbind(Web web) {
        bound.remove(web);
    }
}
