package game.connection.packets.containers.images;

import game.world.BPoint;
import game.world.bodies.Body;
import game.world.constraints.Edge;
import game.world.constraints.FixedEdge;
import javafx.util.Pair;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BodyImage {
    public ArrayList<EdgeImage> edges;
    public ArrayList<Pair<Double, ArrayRealVector>> points;

    public BodyImage(Body body) {
        edges = getEdgesImage(body);
        points = getPointsImage(body);
    }

    @NotNull
    private ArrayList<EdgeImage> getEdgesImage(@NotNull Body body) {
        ArrayList<EdgeImage> edgesImage = new ArrayList<>();
        for (Edge e : body.getEdges()) {
            edgesImage.add(new EdgeImage(e));
        }
        return edgesImage;
    }

    @NotNull
    private ArrayList<Pair<Double, ArrayRealVector>> getPointsImage(@NotNull Body body) {
        ArrayList<Pair<Double, ArrayRealVector>> points = new ArrayList<>();
        for (BPoint point : body.getPoints()) {
            points.add(new Pair<>(point.getMass(), point.getPos()));
        }
        return points;
    }

    public Body getIsolatedBody() {
        Body body = new Body();
        for (Pair<Double, ArrayRealVector> point : points) {
            body.addPoint(point.getKey(), point.getValue());
        }
        ArrayList<BPoint> bodyPoints = body.getPoints();
        for (EdgeImage e : edges) {
            body.addEdge(new FixedEdge(bodyPoints.get(e.index1), bodyPoints.get(e.index2), e.distance));
        }
        return body;
    }

    public void connectBody(Body body) {

    }
}