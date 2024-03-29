package game.connection.packets.wrappers.containers.images.bodies;

import game.connection.packets.messages.EdgeM;
import game.connection.packets.messages.ServerMessage;
import game.connection.packets.wrappers.containers.images.ArrayRealVectorImage;
import game.connection.packets.wrappers.containers.images.edges.EdgeImage;
import game.connection.packets.wrappers.containers.images.edges.FixedEdgeImage;
import game.world.BPoint;
import game.world.Point;
import game.world.World;
import game.world.bodies.Body;
import game.world.bodies.Shell;
import game.world.bodies.Turtle;
import game.world.bodies.Web;
import game.world.constraints.Edge;
import game.world.constraints.FixedEdge;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static game.util.DevConfig.doublePrecision;
import static game.util.Maths.getVector;

public abstract class BodyImage<T extends Body> implements Serializable {

    public ArrayList<EdgeImage> edges;
    public ArrayList<Point> points;
    public ArrayList<Integer> bound;
    public transient T body;

    public BodyImage(T body) {
        makeImage(body);
    }

    public BodyImage(@NotNull ServerMessage.WorldM.BodyM message) {
        edges = new ArrayList<>();
        for (EdgeM edge : message.getEdgeList()) {
            edges.add(EdgeImage.getImageFromMessage(edge));
        }
        points = new ArrayList<>();
        for (int i = 0; i < message.getPointCount(); i++) {
            points.add(new Point(message.getPoint(i).getMass() / doublePrecision,
                    ArrayRealVectorImage.getVector(message.getPoint(i).getPos())));
        }
        bound = new ArrayList<>();
        bound.addAll(message.getBoundList());
    }

    public static BodyImage<?> getImageFromMessage(ServerMessage.WorldM.BodyM message) {
        if (message.hasShell()) {
            return new ShellImage(message);
        } else if (message.hasTurtle()) {
            return new TurtleImage(message);
        } else if (message.hasWeb()) {
            return new WebImage(message);
        }
        return null;
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
    ArrayList<Point> getPointsImage(@NotNull Body body) {
        ArrayList<Point> points = new ArrayList<>();
        for (BPoint point : body.getPoints()) {
            points.add(new Point(point.getMass(), point.getPos().copy()));
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
        for (Point point : points) {
            body.addPoint(point.getMass(), point.getPos());
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

    public abstract ServerMessage.WorldM.BodyM getMessage();
}