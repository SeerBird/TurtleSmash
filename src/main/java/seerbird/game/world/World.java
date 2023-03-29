package seerbird.game.world;

import javafx.util.Pair;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import seerbird.game.Config;
import seerbird.game.EventManager;

import java.util.*;

public class World {
    ArrayList<TurtleBody> bodies;
    ArrayList<Web> webs;
    EventManager handler;
    private Turtle player;

    public World(EventManager handler) {
        this.handler = handler;
        bodies = new ArrayList<>();
        webs = new ArrayList<>();
        testgen();
    }


    public void update() {
        //System.out.println(turtleBodyIntersects(21, 31));
        updateBodies();
        updateWebs();
    }

    private void testgen() {
        /*
        player = new Turtle(this, 20, 30);
        bodies.add(player);
        bodies.add(new Turtle(this, 50, 230));
         */
    }

    public EventManager getHandler() {
        return handler;
    }

    public ArrayList<TurtleBody> getBodies() {
        return this.bodies;
    }

    public ArrayList<Web> getWebs() {
        return this.webs;
    }

    private void updateBodies() {
        // move and rotate
        ArrayRealVector pos;
        for (TurtleBody b : bodies) {
            gravitate(b.pos);
            b.update();
        }
    }

    private void gravitate(ArrayRealVector pos) {
        for (TurtleBody g : bodies) {
            ArrayRealVector dist;
            dist = getBorderDistance(g.getPos(), pos);
            pos.combineToSelf(1, 1,
                    dist.mapMultiplyToSelf(Config.gravity * g.getMass() / Math.pow(Math.max(dist.getNorm(), Config.minGravityDistance), 3)));
        }
    }

    private void updateWebs() {
        for (Web w : webs) {
            for (Pair<ArrayRealVector, ArrayRealVector> link : w.getLinks()) {
                gravitate(link.getKey());
            }
            w.update();
        }
    }

    public @Nullable TurtleBody turtleBodyIntersects(ArrayRealVector pos) {
        ArrayRealVector dist;
        for (TurtleBody b : bodies) {
            dist = getBorderDistance(pos, b.getPos());
            if (b.getShape().contains(dist.getEntry(0), dist.getEntry(1))) {
                return b;
            }
        }
        return null;
    }

    public @Nullable TurtleBody turtleBodyIntersects(double x, double y) {
        ArrayRealVector dist;
        for (TurtleBody b : bodies) {
            dist = getBorderDistance(new ArrayRealVector(new Double[]{x, y}), b.getPos());
            if (b.getShape().contains(dist.getEntry(0), dist.getEntry(1))) {
                return b;
            }
        }
        return null;
    }

    public boolean turtleBodyIntersects(ArrayRealVector pos, @NotNull TurtleBody body) {
        ArrayRealVector dist;
        dist = getBorderDistance(pos, body.getPos());
        return body.getShape().contains(dist.getEntry(0), dist.getEntry(1));
    }

    public boolean turtleBodyIntersects(double x, double y, @NotNull TurtleBody body) {
        ArrayRealVector dist = getBorderDistance(x, y, body.getPos());
        return body.getShape().contains(dist.getEntry(0), dist.getEntry(1));
    }

    public Turtle getPlayer() {
        return this.player;
    }

    public void borderLink(@NotNull ArrayRealVector pos) {
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

    public ArrayRealVector getBorderDistance(@NotNull ArrayRealVector pos1, @NotNull ArrayRealVector pos2) {
        double x1 = pos2.getEntry(0);
        double y1 = pos2.getEntry(1);
        double x2 = pos1.getEntry(0);
        double y2 = pos1.getEntry(1);
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

    public double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }

    public ArrayRealVector getBorderDistance(double x, double y, @NotNull ArrayRealVector pos2) {
        double x1 = pos2.getEntry(0);
        double y1 = pos2.getEntry(1);
        double dx;
        double dy;
        if (Math.abs(x - x1) < Config.WIDTH / 2.0) {
            dx = x - x1;
        } else {
            if (x - x1 > 0) {
                dx = x - x1 - Config.WIDTH;
            } else {
                dx = x - x1 + Config.WIDTH;
            }
        }
        if (Math.abs(y - y1) < Config.HEIGHT / 2.0) {
            dy = y - y1;
        } else {
            if (y - y1 > 0) {
                dy = y - y1 - Config.HEIGHT;
            } else {
                dy = y - y1 + Config.HEIGHT;
            }
        }
        return new ArrayRealVector(new Double[]{dx, dy});
    }
}
