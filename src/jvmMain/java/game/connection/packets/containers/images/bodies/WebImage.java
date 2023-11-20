package game.connection.packets.containers.images.bodies;

import game.connection.packets.containers.images.edges.ControlEdgePointer;
import game.connection.packets.containers.images.edges.EdgeImage;
import game.connection.packets.containers.images.edges.WorldEdgeImage;
import game.connection.packets.containers.images.edges.BodyEdgePointer;
import game.world.BPoint;
import game.world.bodies.Web;
import game.world.constraints.Edge;
import javafx.util.Pair;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.util.ArrayList;

public class WebImage extends BodyImage {
    public BodyEdgePointer target;
    public double control;
    public WorldEdgeImage sourceEdge;
    public WorldEdgeImage targetEdge1;
    public WorldEdgeImage targetEdge2;
    public boolean isGrowing;

    public WebImage(Web web) {
        points = getPointsImage(web);
        edges = new ArrayList<>();
        for (Edge e : web.getEdges()) {
            edges.add(new ControlEdgePointer(e));
        }
        control = web.getControl().getValue();
        Edge edge;
        if ((edge = web.getTarget()) != null) {
            target = new BodyEdgePointer(edge);
            targetEdge1 = new WorldEdgeImage(web.targetEdge1);
            targetEdge2 = new WorldEdgeImage(web.targetEdge2);
        }
        if ((edge = web.getSourceEdge()) != null) {
            sourceEdge = new WorldEdgeImage(edge);
        }
        isGrowing = web.isGrowing();
    }

    @Override
    public Web getIsolatedBody() {
        Web web = new Web();
        web.rest_d.setValue(control);
        for (Pair<Double, ArrayRealVector> point : points) {
            web.addPoint(point.getKey(), point.getValue());
        }
        MutableDouble control = web.getControl();
        ArrayList<BPoint> bodyPoints = web.getPoints();
        for (EdgeImage e : edges) {
            if (e instanceof ControlEdgePointer) {
                web.addEdge(bodyPoints.get(((ControlEdgePointer) e).index1), bodyPoints.get(((ControlEdgePointer) e).index2), control);
            }
        }
        this.body = web;
        web.setGrowing(isGrowing);
        return web;
    }

    @Override
    public void connectBody() {
        Web web = (Web) body;
        if (target != null) {
            web.targetEdge1 = targetEdge1.getEdge();
            web.targetEdge2 = targetEdge2.getEdge();
            web.target = target.findEdge();
        }
        if (sourceEdge != null) {
            web.sourceEdge = sourceEdge.getEdge();
            web.source = web.sourceEdge.getEdge1();
        }
    }
}
