package game.world;

import game.DevConfig;
import game.Player;
import game.connection.packets.containers.WorldData;
import game.util.Maths;
import game.world.bodies.*;
import game.connection.packets.containers.images.bodies.BodyImage;
import game.world.constraints.Edge;
import javafx.util.Pair;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static game.DevConfig.HEIGHT;
import static game.DevConfig.WIDTH;
import static game.util.Maths.*;

public final class World {
    static ArrayList<Body> bodies = new ArrayList<>();
    static ArrayList<Body> toRemove = new ArrayList<>();
    static ArrayList<Body> toAdd = new ArrayList<>();

    public static void update() { // make it all multiplied by dt?
        //any required magic should be done before movement
        gravitate();
        //region Movement and fading
        for (Body b : bodies) {
            //fadeBodies(b); // remove irrelevant bodies
            b.move(); // keep moving based on last update
            //wrapAround(b); // teleport through border
        }
        //endregion
        //region Constraints and collisions
        Body b1;
        Body b2;
        Body body1;
        Body body2;
        for (int i = 0; i < bodies.size(); i++) {
            b1 = bodies.get(i);
            b1.constrain(); //optimise? I made constrain give a boolean return to help with that
            for (int j = i + 1; j < bodies.size(); j++) {
                b2 = bodies.get(j);
                //region check if the bodies should collide and figure out which one will be the main actor(body1)
                if (!b1.collides(b2)) {
                    if (!b2.collides(b1)) {
                        continue;
                    } else {
                        body1 = b2;
                        body2 = b1;
                    }
                } else {
                    body1 = b1;
                    body2 = b2;
                }
                //endregion
                CollisionData collision = checkCollision(body1, body2);
                if (collision != null) {
                    body1.collide(collision);
                }
            }
        }
        //endregion
        //region Remove and add bodies. Separate this? Think about the states I want the lists to be in at the start and end of the world update cycle
        for (Body b : toRemove) {
            bodies.remove(b);
        }
        bodies.addAll(toAdd);
        toRemove.clear();
        toAdd.clear();
        //endregion
    }

    public static void clear() {
        toRemove.addAll(bodies);
        toAdd.clear();
    }

    public static ArrayList<Body> getBodies() {
        return bodies;
    }

    public static void removeBody(Body b) {
        toRemove.add(b);
    }

    public static void addBody(Body b) {
        toAdd.add(b);
    }

    //region Gravitation
    static void gravitate() {//optimize
        Body b1;
        Body b2;
        for (int i = 0; i < bodies.size(); i++) {
            b1 = bodies.get(i);
            if (b1.gravitates()) {
                for (int j = i + 1; j < bodies.size(); j++) {
                    b2 = bodies.get(j);
                    if (b2.gravitates()) {
                        b1.gravitate(b2);
                    }
                }
            }
        }
    }
    //endregion

    //region Collisions
    @Nullable
    static CollisionData checkCollision(@NotNull Body b1, @NotNull Body b2) { // separating axis theorem, should only work for convex shapes
        //pray for your turtles
        //region Returns and locals
        ArrayRealVector collisionAxis = null;
        Edge collisionEdge = null;
        BPoint collisionVertex = null;

        double distance;
        boolean collided = true;
        ArrayRealVector normalAxis;
        ArrayRealVector parallelAxis;
        ArrayList<Edge> edges1 = b1.getSides();
        ArrayList<Edge> edges2 = b2.getSides();
        double minDistance = Double.MAX_VALUE; // from vertex to edge in the direction of the axis
        ArrayList<Pair<Double, BPoint>> projection2;
        //endregion
        //region Web collision algorithm
        if (b1.getClass() == Web.class) {
            if (((Web) b1).isSticky()) {
                BPoint sticky = ((Web) b1).getSticky();
                for (Edge e : b2.getSides()) {
                    //region get the axis perpendicular to Edge e and project the sticky point and the body onto it
                    double projection1;
                    parallelAxis = e.getEdge1().getDistance(e.getEdge2()); // first vertex to second
                    parallelAxis.mapMultiplyToSelf(1 / parallelAxis.getNorm()); //normalize
                    normalAxis = new ArrayRealVector(new Double[]{
                            parallelAxis.getEntry(1), -parallelAxis.getEntry(0)});
                    projection1 = sticky.project(normalAxis); //double, point is sticky
                    projection2 = b2.project(normalAxis); //ArrayList<Pair<Double, BPoint>>
                    //endregion
                    //region check collision using projection overlap and store the potential collision edge and overlap
                    if ((projection1 > projection2.get(0).getKey()) && (projection1 < projection2.get(1).getKey())) {
                        if (sticky.project(parallelAxis) > e.getEdge1().project(parallelAxis) &&
                                sticky.project(parallelAxis) < e.getEdge2().project(parallelAxis)) {
                            distance = Math.abs(sticky.project(normalAxis) - e.getEdge1().project(normalAxis));
                            if (distance < minDistance) {
                                minDistance = distance;
                                collisionEdge = e;
                                collisionAxis = normalAxis;
                            }
                        }
                    } else {
                        collided = false;
                        break;
                    } // no collision
                    //endregion
                }
                //region if no separating axis has been found, add the collision data with minimal overlap to the collision list
                if (collided) {
                    assert collisionEdge != null;
                    return (new CollisionData(sticky, collisionEdge, (ArrayRealVector) collisionAxis.mapMultiplyToSelf(minDistance)));
                }
                //endregion
            }
        }
        //endregion
        //region Non-web collision algorithm
        else {
            Edge edge; //non-webs can collide with sides, not just points
            ArrayList<Pair<Double, BPoint>> projection1;
            for (int i = 0; i < edges1.size() + edges2.size(); i++) {
                //region (iterates through edges of both bodies)
                if (i < edges1.size()) {
                    edge = edges1.get(i);
                } else {
                    edge = edges2.get(i - edges1.size());
                }
                //endregion
                //region get the axis perpendicular to the edge and project both bodies onto it
                normalAxis = edge.getEdge1().getDistance(edge.getEdge2()); // first vertex to second
                double x = normalAxis.getEntry(0);
                normalAxis.setEntry(0, normalAxis.getEntry(1));
                normalAxis.setEntry(1, -x);
                normalAxis.mapMultiplyToSelf(1 / normalAxis.getNorm());
                projection1 = b1.project(normalAxis);
                projection2 = b2.project(normalAxis);
                //the projections are in the form of <projection value, corresponding BPoint>
                //endregion
                //region painstakingly get signed overlap of the two projections, b2 to b1, and record the potential collision vertex
                //region optimization and code cleanup
                BPoint potentialVertex;
                double min1 = projection1.get(0).getKey();
                double max1 = projection1.get(1).getKey();
                double min2 = projection2.get(0).getKey();
                double max2 = projection2.get(1).getKey();
                boolean minOf2Greater = min2 > min1;
                boolean maxOf2Greater = max2 > max1;
                boolean edgeOwnerIs2 = edge.getEdge1().getParentBody() == b2;
                //endregion
                //region if one body is inside the other
                if (minOf2Greater ^ maxOf2Greater) {
                    if (Math.abs(max2 - min1) < Math.abs(max1 - min2)) { //min1 to max2 is shorter
                        if (edgeOwnerIs2) {
                            potentialVertex = projection1.get(0).getValue();
                            distance = max2 - min1;
                        } else {
                            potentialVertex = projection2.get(1).getValue();
                            distance = min1 - max2;
                        }
                    } else {//min2 to max1 is shorter
                        if (edgeOwnerIs2) {
                            potentialVertex = projection1.get(1).getValue();
                            distance = min2 - max1;
                        } else {
                            potentialVertex = projection2.get(0).getValue();
                            distance = max1 - min2;
                        }
                    }//get the shortest distance
                }
                //endregion
                else {
                    //region if body 2 is after body 1
                    if (minOf2Greater) {
                        if (edgeOwnerIs2) {
                            potentialVertex = projection1.get(1).getValue();
                            distance = Math.min(0, min2 - max1);
                        } else {
                            potentialVertex = projection2.get(0).getValue();
                            distance = Math.max(0, max1 - min2);
                        }
                    }
                    //endregion
                    //region if body 1 is after body 2
                    else {
                        if (edgeOwnerIs2) {
                            potentialVertex = projection1.get(0).getValue();
                            distance = Math.max(0, max2 - min1);
                        } else {
                            potentialVertex = projection2.get(1).getValue();
                            distance = Math.min(0, min1 - max2);
                        }
                    }
                    //endregion
                }
                //endregion
                //region if there is overlap, record it and the potential collision vertex and edge
                if (distance != 0) {
                    if (Math.abs(distance) < Math.abs(minDistance)) {
                        minDistance = distance;
                        collisionEdge = edge;
                        collisionAxis = normalAxis;
                        collisionVertex = potentialVertex;
                    }
                }
                //endregion
                //region otherwise, there is no collision, finish checking for this body pair
                else {
                    collided = false;
                    break;
                }
                //endregion
            }
            //region if no separating axis has been found, add the collision data with minimal overlap to the collision list
            if (collided && collisionEdge != null) {
                return (new CollisionData(collisionVertex, collisionEdge, (ArrayRealVector) collisionAxis.mapMultiplyToSelf(minDistance)));
            }
            //endregion
        }
        //endregion
        return null;
    }
    //endregion

    void wrapAround(@NotNull Body b) {
        ArrayList<Pair<Double, BPoint>> projectionX = b.project(Maths.i);
        ArrayList<Pair<Double, BPoint>> projectionY = b.project(j);
        if (projectionX.get(0).getKey() > WIDTH) {
            b.shift(new ArrayRealVector(new Double[]{-projectionX.get(1).getKey(), 0.0}));
        } else if (projectionX.get(1).getKey() < 0) {
            b.shift(new ArrayRealVector(new Double[]{WIDTH - projectionX.get(0).getKey(), 0.0}));
        }
        if (projectionY.get(0).getKey() > HEIGHT) {
            b.shift(new ArrayRealVector(new Double[]{0.0, -projectionY.get(1).getKey()}));
        } else if (projectionY.get(1).getKey() < 0) {
            b.shift(new ArrayRealVector(new Double[]{0.0, HEIGHT - projectionY.get(0).getKey() - 1}));
        }
        if (b.apprVelocity() > WIDTH) {
            b.stop();
        }
    }

    void fadeBodies(@NotNull Body b) {
        ArrayList<Pair<Double, BPoint>> projectionX = b.project(Maths.i);
        ArrayList<Pair<Double, BPoint>> projectionY = b.project(j);
        //boolean gone = false;
        if (projectionX.get(0).getKey() > WIDTH) {
            b.decreaseRelevance(1 / 60.0);
            //gone = true;
        } else if (projectionX.get(1).getKey() < 0) {
            b.decreaseRelevance(1 / 60.0);
            //gone = true;
        }
        if (projectionY.get(0).getKey() > HEIGHT) {
            b.decreaseRelevance(1 / 60.0);
            //gone = true;
        } else if (projectionY.get(1).getKey() < 0) {
            b.decreaseRelevance(1 / 60.0);
            //gone = true;
        }
        /*
        if (!gone) {
            b.resetRelevance();
        }

         */
        if (b.getRelevance() <= 0) {
            removeBody(b);
        }
    }

    public ArrayRealVector getBorderDistance(@NotNull ArrayRealVector pos1, @NotNull ArrayRealVector pos2) {
        double x1 = pos1.getEntry(0);
        double y1 = pos1.getEntry(1);
        return getBorderDistance(x1, y1, pos2);
    }

    public ArrayRealVector getBorderDistance(double x1, double y1, @NotNull ArrayRealVector pos2) {
        double x2 = pos2.getEntry(0);
        double y2 = pos2.getEntry(1);
        double dx;
        double dy;
        if (Math.abs(x2 - x1) < WIDTH / 2.0) {
            dx = x2 - x1;
        } else {
            if (x2 - x1 > 0) {
                dx = x2 - x1 - WIDTH;
            } else {
                dx = x2 - x1 + WIDTH;
            }
        }
        if (Math.abs(y2 - y1) < HEIGHT / 2.0) {
            dy = y2 - y1;
        } else {
            if (y2 - y1 > 0) {
                dy = y2 - y1 - HEIGHT;
            } else {
                dy = y2 - y1 + HEIGHT;
            }
        }
        return new ArrayRealVector(new Double[]{dx, dy});
    }

    public static ArrayRealVector getDistance(ArrayRealVector pos1, @NotNull ArrayRealVector pos2) {
        return pos2.combine(1, -1, pos1);
    }

    public static void set(@NotNull WorldData data) {
        bodies.clear();
        for (BodyImage body : data.bodyImages) {
            bodies.add(body.getIsolatedBody());
        }
        for (int i = 0; i < data.bodyImages.size(); i++) {
            data.bodyImages.get(i).connectBody();
        }
    }

    public static void spawn(ArrayRealVector pos) {
        //new Box(pos, new ArrayRealVector(new Double[]{40.0, 0.0}), new ArrayRealVector(new Double[]{0.0, 40.0}));
        new Shell(pos,randomUnitVector(), null);
    }

    public static void playerSpawn(@NotNull Player player) {
        double x, y;
        if(player.getBody()!=null){
            player.getBody().delete();
        }
        x = Math.random()*2-1;
        y = Math.signum(Math.random() - 0.5) * Math.pow(1 - x * x, 0.5);
        ArrayRealVector pos = (ArrayRealVector) getVector(x,y).mapMultiplyToSelf(Math.pow(WIDTH*WIDTH+HEIGHT*HEIGHT,0.5)/2+Math.random()* DevConfig.playerSpawnSpread);
        Turtle turtle = new Turtle(pos.add(getVector(WIDTH/2.0,HEIGHT/2.0)), player);
        turtle.accelerate(pos.mapMultiply(-DevConfig.approxPlayerSpawnVelocity / pos.getNorm()) // get velocity to the center of the screen
                .combine(1, DevConfig.playerSpawnVelocitySpread, getVector(Math.random(), Math.random()))); // add spread
        player.setBody(turtle);
    }


}
