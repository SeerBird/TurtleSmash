package seerbird.game.world.bodies;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import seerbird.game.Config;
import seerbird.game.world.CollisionData;
import seerbird.game.world.VPoint;
import seerbird.game.world.World;
import seerbird.game.world.constraints.DistanceConstraint;

import java.util.ArrayList;

public class Web extends Body {
    VPoint source;
    boolean attached;
    boolean isGrowing;
    ArrayList<Boolean> stuck;

    // last point is attached to source
    public Web(@NotNull World world, @NotNull VPoint source, ArrayRealVector velocity) {
        super(world);
        stuck = new ArrayList<>();
        gravitates = false;
        this.source = source;
        addPoint(new VPoint(this, 1, source.getPos()));
        points.get(0).accelerate(velocity);
        attached = true;
        isGrowing = true;
    }

    @Override
    public ArrayList<DistanceConstraint> getSides() {// do not return sides that are stuck
        ArrayList<DistanceConstraint> sides = new ArrayList<>();
        for (int i = 0; i < edges.size(); i++) {
            if (!stuck.get(i)) {
                sides.add(edges.get(i));
            }
        }
        return sides;
    }

    @Override
    public void addEdge(VPoint p1, VPoint p2) {
        super.addEdge(p1, p2);
        stuck.add(false);
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
    public void collide(@NotNull CollisionData collision) {
        super.collide(collision);
        addEdge(collision.getEdge1(), collision.getVertex());
        stuck.set(stuck.size() - 1, true);
        addEdge(collision.getEdge1(), collision.getVertex());
        stuck.set(stuck.size() - 1, true);
        if (collision.getEdge1().getParent() == this) {
            stuck.set(edges.indexOf(collision.getEdge()), true);
        }
    }

    public boolean isGrowing() {
        return isGrowing;
    }

    public void fix() {
        if (isGrowing) {
            isGrowing = false;
            addEdge(points.get(points.size() - 1), source);
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
