package game.connection.packets.containers.images.bodies;

import game.connection.packets.containers.images.edges.EdgeImage;
import game.connection.packets.containers.images.edges.FixedEdgeImage;
import game.world.BPoint;
import game.world.World;
import game.world.bodies.Body;
import game.world.bodies.Shell;
import game.world.bodies.Turtle;
import game.world.bodies.Web;
import game.world.constraints.Edge;
import game.world.constraints.FixedEdge;
import javafx.util.Pair;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static game.util.Maths.getVector;

public abstract class BodyImage<T extends Body> {
    public ArrayList<EdgeImage> edges;
    public ArrayList<ArrayList<Double>> points;
    public ArrayList<Integer> bound;
    public transient T body;

    public BodyImage(Body body) {
        edges = getEdgesImage(body);
        points = getPointsImage(body);
        bound = new ArrayList<>();
        for (Web web : body.bound) {
            bound.add(World.getBodies().indexOf(web));
        }
    }

    protected BodyImage() {
    }

    @NotNull
    ArrayList<EdgeImage> getEdgesImage(@NotNull Body body) {
        ArrayList<EdgeImage> edgesImage = new ArrayList<>();
        for (Edge e : body.getEdges()) {
            edgesImage.add(new FixedEdgeImage(e));
        }
        return edgesImage;
    }

    @NotNull
    ArrayList<ArrayList<Double>> getPointsImage(@NotNull Body body) {
        ArrayList<ArrayList<Double>> points = new ArrayList<>();
        ArrayList<Double> pointData=new ArrayList<>();
        for (BPoint point : body.getPoints()) {
            pointData.add(point.getX());
            pointData.add(point.getY());
            pointData.add(point.getMass());
            points.add(new ArrayList<>(pointData));
            pointData.clear();
        }
        return points;
    }

    public static Class<? extends BodyImage> getImageClass(@NotNull Body body) {
        Class<? extends Body> clazz = body.getClass();
        if (clazz.equals(Web.class)) {
            return WebImage.class;
        } else if (clazz.equals(Turtle.class)) {
            return TurtleImage.class;
        } else if (clazz.equals(Shell.class)) {
            return ShellImage.class;
        } else {
            return BodyImage.class;
        }
    }

    public abstract T getIsolatedBody();

    public void addPoints(Body body) {
        for (ArrayList<Double> point: points) {
            body.addPoint(point.get(2),getVector( point.get(0),point.get(1)));
        }
    }

    public void addEdges(Body body) {
        ArrayList<BPoint> bodyPoints = body.getPoints();
        for (EdgeImage e : edges) {
            if (e instanceof FixedEdgeImage) {
                body.addEdge(new FixedEdge(bodyPoints.get(((FixedEdgeImage) e).index1), bodyPoints.get(((FixedEdgeImage) e).index2), ((FixedEdgeImage) e).distance));
            }
        }
    }

    public void connectBody() {
        for (Integer i : bound) {
            body.bound.add((Web) World.getBodies().get(i));
        }
    }
}