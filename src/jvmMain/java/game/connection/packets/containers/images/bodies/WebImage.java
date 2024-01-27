package game.connection.packets.containers.images.bodies;

import game.connection.packets.containers.images.edges.ControlEdgePointer;
import game.connection.packets.containers.images.edges.EdgeImage;
import game.connection.packets.containers.images.edges.WorldEdgeImage;
import game.connection.packets.containers.images.edges.BodyEdgePointer;
import game.world.BPoint;
import game.world.World;
import game.world.bodies.Web;
import game.world.constraints.Edge;
import javafx.util.Pair;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.util.ArrayList;

public class WebImage extends BodyImage<Web> {
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
            if (World.getBodies().contains(edge.getEdge1().getParentBody())) {
                target = new BodyEdgePointer(edge);
                targetEdge1 = new WorldEdgeImage(web.targetEdge1);
                targetEdge2 = new WorldEdgeImage(web.targetEdge2);
            }
        }
        if ((edge = web.getSourceEdge()) != null) {
            sourceEdge = new WorldEdgeImage(edge);
        }
        isGrowing = web.isGrowing();
    }

    @Override
    public Web getIsolatedBody() {
        body = new Web();
        body.rest_d.setValue(control);
        addPoints(body);
        MutableDouble control = body.getControl();
        ArrayList<BPoint> bodyPoints = body.getPoints();
        for (EdgeImage e : edges) {
            if (e instanceof ControlEdgePointer) {
                body.addEdge(bodyPoints.get(((ControlEdgePointer) e).index1), bodyPoints.get(((ControlEdgePointer) e).index2), control);
            }
        }
        body.setGrowing(isGrowing);
        return body;
    }

    @Override
    public void connectBody() {
        if (target != null) {
            body.targetEdge1 = targetEdge1.getEdge();
            body.targetEdge2 = targetEdge2.getEdge();
            body.target = target.findEdge();
        }
        if (sourceEdge != null) {
            body.sourceEdge = sourceEdge.getEdge();
            body.source = body.sourceEdge.getEdge1();
        }
    }
}
