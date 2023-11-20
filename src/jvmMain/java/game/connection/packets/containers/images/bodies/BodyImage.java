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

public abstract class BodyImage {
    public ArrayList<EdgeImage> edges;
    public ArrayList<Pair<Double, ArrayRealVector>> points;
    transient Body body;

    public BodyImage(Body body) {
        edges = getEdgesImage(body);
        points = getPointsImage(body);
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
    ArrayList<Pair<Double, ArrayRealVector>> getPointsImage(@NotNull Body body) {
        ArrayList<Pair<Double, ArrayRealVector>> points = new ArrayList<>();
        for (BPoint point : body.getPoints()) {
            points.add(new Pair<>(point.getMass(), point.getPos()));
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

    public abstract Body getIsolatedBody();
    public void addPoints(Body body){
        for (Pair<Double, ArrayRealVector> point : points) {
            body.addPoint(point.getKey(), point.getValue());
        }
    }
    public void addEdges(Body body){
        ArrayList<BPoint> bodyPoints = body.getPoints();
        for (EdgeImage e : edges) {
            if (e instanceof FixedEdgeImage) {
                body.addEdge(new FixedEdge(bodyPoints.get(((FixedEdgeImage) e).index1),
                        bodyPoints.get(((FixedEdgeImage) e).index2),
                        ((FixedEdgeImage) e).distance));
            }
        }
    }

    public void connectBody() {

    }
}