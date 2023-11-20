package game.world.bodies;

import game.Config;
import game.connection.packets.containers.images.bodies.BodyImage;
import game.connection.packets.containers.images.bodies.WebImage;
import game.world.BPoint;
import game.world.CollisionData;
import game.world.constraints.ControlEdge;
import game.world.constraints.Edge;
import game.world.constraints.FixedEdge;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class Web extends Body {
    @Nullable
    public BPoint source; //I don't like the fact that all of this is public
    public Edge target;
    public Edge sourceEdge;
    public Edge targetEdge1;
    public Edge targetEdge2;
    boolean isGrowing;
    public MutableDouble rest_d;

    // points are ordered end to source
    public Web(@NotNull BPoint source, ArrayRealVector velocity) {
        super();
        this.source = source;
        addPoint(new BPoint(this, 1, source.getPos()));
        points.get(0).accelerate(velocity);
        isGrowing = true;
        rest_d = new MutableDouble(Config.webRestNodeDistance);
        relevance = 400; // do something other than relevance. this is stupid. kinda. idk. maybe it's fine
    }

    public Web() {
        points = new ArrayList<>();
        edges = new ArrayList<>();
        acceleration = new ArrayRealVector(2);
        movement = new ArrayRealVector(2);
        center = new ArrayRealVector(2);
        velocity = new ArrayRealVector(2);
        relevance = 400;
        mass = 0;
        centerMoved = true;
        isGrowing = true;
        rest_d = new MutableDouble(Config.webRestNodeDistance);
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
                double distance = dist.getNorm() / Config.webRestNodeDistance; // not in pixels but in rest distances
                if (distance > 1) {// if the gap is greater than a web edge rest distance
                    dist.mapMultiplyToSelf(1 / distance); // get the rest distance vector in the right direction
                    //region add points while there is a gap and the length limit has not been reached
                    for (int i = 1; i < distance; i++) {
                        if (points.size() >= Config.webLengthLimit) {
                            sourceEdge = new ControlEdge(source, points.get(points.size() - 1), rest_d);
                            isGrowing = false;
                            break;
                        }
                        addPoint(new BPoint(this, 1, root.combineToSelf(1, 1, dist)));
                        addEdge(points.get(points.size() - 2), points.get(points.size() - 1), rest_d);
                    }
                    //endregion
                }
                //endregion
            }
        }
        //endregion
        //region behavior when disconnected from source turtle
        else {
            rest_d.subtract(Config.webDecay);
            if (rest_d.doubleValue() < 0) {
                delete();
            }
        }
        //endregion
    }

    @Override
    public void delete() {
        super.delete();
        if (target != null) {
            Body victim = target.getEdge1().getParentBody();
            if (victim.getClass() == Shell.class) {
                ((Shell) victim).unbind(this);
            }
        }
        //source should already be null
    }

    @Override
    public void collide(@NotNull CollisionData collision) {
        //region get the two things that are sticking to one another
        target = collision.edge;
        BPoint sticky = collision.getVertex();
        //endregion
        //region move the sticky point onto its projection onto the target edge
        ArrayRealVector distance = target.getEdge1().getDistance(target.getEdge2());
        distance.mapMultiply(target.getRestDistance() / distance.getNorm());
        double edgeX = distance.getEntry(0);
        double edgeY = distance.getEntry(1);
        // should be 0 to 1, indicating where between edge1 and edge2 the vertex projection is
        double placement = (distance.getNorm() > 0) ? (Math.abs(edgeX) >= Math.abs(edgeY)) ? (sticky.getX() - target.getEdge1().getX()) / (edgeX) : (sticky.getY() - target.getEdge1().getY()) / (edgeY) : 0.5;
        sticky.setPos(target.getEdge1().getPos().combine(1, 1, distance.mapMultiply(placement)));
        //endregion
        //region create the two connections between the sticky point and the ends of the target edge
        targetEdge1 = new FixedEdge(sticky, target.getEdge1(), distance.getNorm() * placement);
        targetEdge2 = new FixedEdge(sticky, target.getEdge2(), distance.getNorm() * (1 - placement));
        if (target.getEdge1().getParentBody().getClass() == Shell.class) {
            ((Shell) target.getEdge1().getParentBody()).addBinder(this);
        }
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
        return body.getClass() != Web.class && isSticky();
    }

    public MutableDouble getControl() {
        return rest_d;
    }
}
