package seerbird.game.world;

import javafx.util.Pair;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import seerbird.game.Config;
import seerbird.game.EventManager;
import seerbird.game.world.bodies.Body;
import seerbird.game.world.constraints.DistanceConstraint;

import java.util.*;

public class World {
    ArrayList<Body> bodies;
    ArrayList<Web> webs;
    ArrayList<Body> graviBody;
    EventManager handler;

    public World(EventManager handler) {
        this.handler = handler;
        bodies = new ArrayList<>();
        webs = new ArrayList<>();
        testgen();
    }


    public void update() {
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

    private void testgen() {
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
        if (collision.vertex.getParent().getClass() == Body.class && collision.edge.getPoints().getKey().getParent().getClass() == Body.class) {
            collision.vertex.getParent().collide(collision);
        }
        //sounds, particles, and other stuff
    }

    ArrayList<CollisionData> checkCollisions(@NotNull Body b1) { // separating axis theorem, only works for convex shapes
        ArrayList<DistanceConstraint> edges1 = b1.getEdges(); // not necessarily all DistanceConstraints of a body
        //returns
        ArrayList<CollisionData> collisions = new ArrayList<>();
        double mindistance = Double.MAX_VALUE; // from vertex to edge in the direction of the axis
        ArrayRealVector collisionAxis = null;
        DistanceConstraint collisionEdge = null;
        VPoint collisionVertex = null;
        for (Body b2 : bodies) {
            if (b2 == b1) {
                continue;
            } // don't collide with yourself ;)
            ArrayList<DistanceConstraint> edges2 = b2.getEdges();
            ArrayRealVector axis;
            ArrayList<Pair<Double, VPoint>> projection1;
            ArrayList<Pair<Double, VPoint>> projection2;
            DistanceConstraint edge;
            double distance; // between the two projections. collision on negative values
            boolean collided = true;
            for (int i = 0; i < edges1.size() + edges2.size(); i++) {
                if (i < edges1.size()) {
                    edge = edges1.get(i);
                } else {
                    edge = edges2.get(i - edges1.size());
                } // iterate through all edges
                axis = edge.getPoints().getKey().getDistance(edge.getPoints().getValue()); // first vertex to second
                double x = axis.getEntry(0);
                axis.setEntry(0, axis.getEntry(1));
                axis.setEntry(1, -x); // axis rotated -90 degrees
                axis.mapMultiplyToSelf(1 / axis.getNorm()); // normalise
                projection1 = b1.project(axis);
                projection2 = b2.project(axis);// maybe change the methods to not take references? efficient but seems to decrease readability
                if (projection2.get(0).getKey() > projection1.get(0).getKey()) { // min1<min2
                    distance = Math.max(0, projection1.get(1).getKey() - projection2.get(0).getKey()); // positive on collision
                } else {
                    distance = Math.min(0, projection1.get(0).getKey() - projection2.get(1).getKey()); // negative on collision
                } // get distance between intervals
                if (distance != 0) {
                    if (Math.abs(distance) < Math.abs(mindistance)) {
                        mindistance = distance;
                        collisionEdge = edge;
                        collisionAxis = axis;
                        if (edge.getPoints().getKey().getParent() == b1) {
                            if (distance < 0) {
                                collisionVertex = projection2.get(0).getValue();
                            } else {
                                collisionVertex = projection2.get(1).getValue();
                                mindistance *= -1;
                            }
                        } else {
                            if (distance < 0) {
                                collisionVertex = projection1.get(1).getValue();
                                mindistance *= -1;
                            } else {
                                collisionVertex = projection1.get(0).getValue();
                            }
                        } // get collision vertex
                    }
                } else {
                    collided = false;
                    break;
                }
            }
            if (collided && collisionEdge != null) {// unnecessary collisionEdge check? shows a warning, I could leave the warning be as it is unrealistic
                collisions.add(new CollisionData(collisionVertex, collisionEdge, (ArrayRealVector) collisionAxis.mapMultiplyToSelf(mindistance)));
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

    public double getSimpleDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }
}
