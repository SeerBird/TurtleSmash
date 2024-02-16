package game.connection.packets.wrappers.containers.images.bodies;

import game.connection.packets.wrappers.containers.images.edges.EdgeImage;
import game.connection.packets.wrappers.containers.images.edges.FixedEdgeImage;
import game.world.BPoint;
import game.world.World;
import game.world.bodies.Body;
import game.world.bodies.Shell;
import game.world.bodies.Turtle;
import game.world.bodies.Web;
import game.world.constraints.Edge;
import game.world.constraints.FixedEdge;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;

import static game.util.Maths.getVector;

public abstract class BodyImage<T extends Body> implements Serializable {
    public static final double pointCoordPrecision = 10.0;

    public ArrayList<EdgeImage> edges;
    public ArrayList<ArrayList<Short>> points;
    public ArrayList<Integer> bound;
    public transient T body;

    public BodyImage(T body) {
        makeImage(body);
    }

    public abstract void makeImage(T body);

    public abstract T getIsolatedBody();

    @NotNull
    ArrayList<EdgeImage> getEdgesImage(@NotNull Body body) {
        ArrayList<EdgeImage> edgesImage = new ArrayList<>();
        for (Edge e : body.getEdges()) {
            edgesImage.add(new FixedEdgeImage(e));
        }
        return edgesImage;
    }

    @NotNull
    ArrayList<ArrayList<Short>> getPointsImage(@NotNull Body body) {
        ArrayList<ArrayList<Short>> points = new ArrayList<>();
        ArrayList<Short> pointData = new ArrayList<>();
        for (BPoint point : body.getPoints()) {
            pointData.add((short) (point.getX() * pointCoordPrecision));
            pointData.add((short) (point.getY() * pointCoordPrecision));
            pointData.add((short) point.getMass());
            points.add(new ArrayList<>(pointData));
            pointData.clear();
        }
        return points;
    }

    public static Class<? extends BodyImage<?>> getImageClass(@NotNull Body body) {
        Class<? extends Body> clazz = body.getClass();
        if (clazz.equals(Web.class)) {
            return WebImage.class;
        } else if (clazz.equals(Turtle.class)) {
            return TurtleImage.class;
        } else if (clazz.equals(Shell.class)) {
            return ShellImage.class;
        } else {
            return ShellImage.class;
        }
    }


    public void addPoints(Body body) {
        for (ArrayList<Short> point : points) {
            body.addPoint(point.get(2), getVector(point.get(0) / pointCoordPrecision, point.get(1) / pointCoordPrecision));
        }
    }

    public void addEdges(Body body) {
        ArrayList<BPoint> bodyPoints = body.getPoints();
        for (EdgeImage e : edges) {
            if (e instanceof FixedEdgeImage) {
                body.addEdge(new FixedEdge(bodyPoints.get(((FixedEdgeImage) e).i1), bodyPoints.get(((FixedEdgeImage) e).i2), ((FixedEdgeImage) e).d));
            }
        }
    }

    public void connectBody() {
        for (Integer i : bound) {
            body.bound.add((Web) World.getBodies().get(i));
        }
    }
}