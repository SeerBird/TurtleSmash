package seerbird.game.world;

import javafx.util.Pair;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import seerbird.game.Config;
import seerbird.game.EventManager;
import seerbird.game.math.Compute;
import seerbird.game.world.bodies.Body;
import seerbird.game.world.bodies.Box;
import seerbird.game.world.constraints.DistanceConstraint;

import java.util.*;

public class World {
    ArrayList<Body> bodies;
    ArrayList<Web> webs;
    ArrayList<Body> graviBodies;
    EventManager handler;

    public World(EventManager handler) {
        this.handler = handler;
        bodies = new ArrayList<>();
        webs = new ArrayList<>();
        graviBodies = new ArrayList<>();
        testgen();
    }


    public void update() {
        boolean nulls = true;
        while (nulls) {
            nulls = bodies.remove(null);
        }
        nulls = true;
        while (nulls) {
            nulls = graviBodies.remove(null);
        }
        nulls = true;
        while (nulls) {
            nulls = webs.remove(null);
        }
        //any required magic is done before movement

        //move it all
        for (Body b : bodies) {
            b.move();
        }
        //constraints and collisions
        for (Body b : bodies) {
            b.constrain(); //optimise? I made constrain give a boolean return to help with that
            for (CollisionData collision : checkCollisions(b)) {
                collide(collision);
            }
        }
    }

    public void testgen() {
        new Box(this, new ArrayRealVector(new Double[]{0.0, 0.0}), new ArrayRealVector(new Double[]{40.0, 0.0}), new ArrayRealVector(new Double[]{0.0, 40.0}));
        new Box(this, new ArrayRealVector(new Double[]{200.0, 100.0}), new ArrayRealVector(new Double[]{40.0, 0.0}), new ArrayRealVector(new Double[]{0.0, 40.0}));
    }

    public EventManager getHandler() {
        return handler;
    }

    public ArrayList<Body> getBodies() {
        return this.bodies;
    }

    public ArrayList<Web> getWebs() {
        return this.webs;
    }

    void collide(@NotNull CollisionData collision) {
        if (collision.vertex.getParent().getClass() == Box.class && collision.edge.getPoints().getKey().getParent().getClass() == Box.class) {
            collision.vertex.getParent().collide(collision);
        }
        //sounds, particles, and other stuff
    }

    ArrayList<CollisionData> checkCollisions(@NotNull Body b1) { // separating axis theorem, only works for convex shapes
        //returns
        ArrayRealVector collisionAxis = null;
        DistanceConstraint collisionEdge = null;
        VPoint collisionVertex = null;
        //locals
        ArrayList<DistanceConstraint> edges1 = b1.getEdges(); // not necessarily all DistanceConstraints of a body?
        ArrayList<DistanceConstraint> edges2;
        ArrayList<CollisionData> collisions = new ArrayList<>();
        double minDistance; // from vertex to edge in the direction of the axis
        ArrayRealVector axis;
        ArrayList<Pair<Double, VPoint>> projection1;
        ArrayList<Pair<Double, VPoint>> projection2;
        DistanceConstraint edge;

        for (Body b2 : bodies) {
            if (b2 == b1) {
                continue;
            } // don't collide with yourself ;)
            minDistance = Double.MAX_VALUE;
            edges2 = b2.getEdges();
            double distance; // between the two projections. collision on negative values
            boolean collided = true;
            for (int i = 0; i < edges1.size() + edges2.size(); i++) {
                {
                    if (i < edges1.size()) {
                        edge = edges1.get(i);
                    } else {
                        edge = edges2.get(i - edges1.size());
                    }
                }// iterates through edges of both bodies
                axis = edge.getPoints().getKey().getDistance(edge.getPoints().getValue()); // first vertex to second
                {
                    double x = axis.getEntry(0);
                    axis.setEntry(0, axis.getEntry(1));
                    axis.setEntry(1, -x);
                    axis.mapMultiplyToSelf(1 / axis.getNorm());
                }// axis rotated -90 degrees and normalised
                {
                    projection1 = b1.project(axis);
                    projection2 = b2.project(axis);
                }//get projections of both bodies onto the axis, in the form of <projection value, corresponding VPoint>
                {
                    if (projection2.get(0).getKey() > projection1.get(0).getKey()) { // min2>min1
                        distance = Math.max(0, projection1.get(1).getKey() - projection2.get(0).getKey()); // positive on collision
                    } else {
                        distance = Math.min(0, projection1.get(0).getKey() - projection2.get(1).getKey()); // negative on collision
                    }
                }// get signed overlap of the projections. overlap is counted b2->b1
                if (distance != 0) {
                    if (Math.abs(distance) < Math.abs(minDistance)) {
                        minDistance = distance;
                        collisionEdge = edge;
                        collisionAxis = axis;
                        {
                            if (edge.getPoints().getKey().getParent() == b1) {
                                if (distance < 0) {
                                    collisionVertex = projection2.get(0).getValue();
                                } else {
                                    collisionVertex = projection2.get(1).getValue();
                                    minDistance *= -1;
                                }
                            } else {
                                if (distance < 0) {
                                    collisionVertex = projection1.get(0).getValue();
                                    minDistance *= -1;
                                } else {
                                    collisionVertex = projection1.get(1).getValue();
                                }
                            }
                        } // get collision vertex and make the distance be from vertex to edge on the axis
                    }
                } else {
                    collided = false;
                    break;
                } // no collision
            }
            if (collided && collisionEdge != null) {// unnecessary collisionEdge check? shows a warning, I could leave the warning be as it is unrealistic
                {
                    if (Compute.areClockwise(collisionEdge.getEdge1().getPos(), collisionEdge.getEdge2().getPos(), collisionVertex.getPos())) {
                        minDistance *= -1;
                    }
                }// flip distance so that the axis direction is from vertex to edge
                collisions.add(new CollisionData(collisionVertex, collisionEdge, (ArrayRealVector) collisionAxis.mapMultiplyToSelf(minDistance)));
            }
        }
        return collisions;
    }

    public void boxConfine(@NotNull ArrayRealVector pos) {
        if (pos.getEntry(0) < 0) {
            pos.setEntry(0, Config.WIDTH + pos.getEntry(0) % Config.WIDTH);
        } else {
            pos.setEntry(0, pos.getEntry(0) % Config.WIDTH);
        }
        if (pos.getEntry(1) < 0) {
            pos.setEntry(1, Config.HEIGHT + pos.getEntry(1) % Config.HEIGHT);
        } else {
            pos.setEntry(1, pos.getEntry(1) % Config.HEIGHT);
        }
    }

    public void wrapAround(Body b) {

    }

    public ArrayRealVector getDistance(@NotNull ArrayRealVector pos1, @NotNull ArrayRealVector pos2) {
        double x1 = pos1.getEntry(0);
        double y1 = pos1.getEntry(1);
        return getDistance(x1, y1, pos2);
    }

    public ArrayRealVector getDistance(double x1, double y1, @NotNull ArrayRealVector pos2) {
        double x2 = pos2.getEntry(0);
        double y2 = pos2.getEntry(1);
        double dx;
        double dy;
        if (Math.abs(x2 - x1) < Config.WIDTH / 2.0) {
            dx = x2 - x1;
        } else {
            if (x2 - x1 > 0) {
                dx = x2 - x1 - Config.WIDTH;
            } else {
                dx = x2 - x1 + Config.WIDTH;
            }
        }
        if (Math.abs(y2 - y1) < Config.HEIGHT / 2.0) {
            dy = y2 - y1;
        } else {
            if (y2 - y1 > 0) {
                dy = y2 - y1 - Config.HEIGHT;
            } else {
                dy = y2 - y1 + Config.HEIGHT;
            }
        }
        return new ArrayRealVector(new Double[]{dx, dy});
    }

    public ArrayRealVector getSimpleDistance(ArrayRealVector pos1, ArrayRealVector pos2) {
        return pos2.copy().combineToSelf(1, -1, pos1);
    }

    public Body getPlayer() {
        return bodies.get(0);
    }
}
