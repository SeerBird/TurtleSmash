package game.connection.packets.wrappers.containers.images.bodies;

import game.connection.packets.messages.EdgeM;
import game.connection.packets.messages.ServerMessage;
import game.connection.packets.wrappers.containers.images.ArrayRealVectorImage;
import game.connection.packets.wrappers.containers.images.edges.BodyEdgePointer;
import game.connection.packets.wrappers.containers.images.edges.ControlEdgePointer;
import game.connection.packets.wrappers.containers.images.edges.EdgeImage;
import game.connection.packets.wrappers.containers.images.edges.WorldEdgeImage;
import game.world.BPoint;
import game.world.Point;
import game.world.World;
import game.world.bodies.Web;
import game.world.constraints.Edge;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.io.Serial;
import java.util.ArrayList;

import static game.util.DevConfig.doublePrecision;

public class WebImage extends BodyImage<Web> {
    @Serial
    private static final long serialVersionUID = 800859;
    public BodyEdgePointer target;
    public Double control;
    public WorldEdgeImage sourceEdge;
    public WorldEdgeImage targetEdge1;
    public WorldEdgeImage targetEdge2;
    public boolean isGrowing;

    public WebImage(Web web) {
        super(web);
    }

    public WebImage(ServerMessage.WorldM.BodyM message) {
        super(message);
        ServerMessage.WorldM.BodyM.WebM webMessage = message.getWeb();
        target = webMessage.hasTarget() ? new BodyEdgePointer(webMessage.getTarget()) : null;
        control = webMessage.getControl() / doublePrecision;
        sourceEdge = webMessage.hasSourceEdge() ? new WorldEdgeImage(webMessage.getSourceEdge()) : null;
        targetEdge1 = webMessage.hasTargetEdge1() ? new WorldEdgeImage(webMessage.getTargetEdge1()) : null;
        targetEdge2 = webMessage.hasTargetEdge2() ? new WorldEdgeImage(webMessage.getTargetEdge2()) : null;
        isGrowing = webMessage.getIsGrowing();
    }

    @Override
    public void makeImage(Web web) {
        points = getPointsImage(web);
        edges = new ArrayList<>();
        bound = new ArrayList<>();
        for (Web binder : web.bound) {
            bound.add(World.getBodies().indexOf(binder));
        }
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
                body.addEdge(bodyPoints.get(((ControlEdgePointer) e).i1), bodyPoints.get(((ControlEdgePointer) e).i2), control);
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

    @Override
    public ServerMessage.WorldM.BodyM getMessage() {
        ServerMessage.WorldM.BodyM.Builder builder = ServerMessage.WorldM.BodyM.newBuilder();
        //region add edges
        for (EdgeImage edge : edges) {
            builder.addEdge(edge.getMessage());
        }
        //endregion
        //region add points
        for (Point point : points) {
            builder.addPoint(ServerMessage.WorldM.BodyM.PointM.newBuilder()
                    .setPos(ArrayRealVectorImage.getMessage(point.getPos()))
                    .setMass((int) (point.getMass() * doublePrecision))
                    .build());
        }
        //endregion
        ServerMessage.WorldM.BodyM.WebM.Builder webBuilder = ServerMessage.WorldM.BodyM.WebM.newBuilder()
                .setControl((int) (control * doublePrecision))
                .setIsGrowing(isGrowing);
        if (target != null) {
            webBuilder.setTarget(target.getMessage().getBep());
        }
        if (targetEdge1 != null) {
            webBuilder.setTargetEdge1(targetEdge1.getMessage().getWei());
        }
        if (targetEdge2 != null) {
            webBuilder.setTargetEdge2(targetEdge2.getMessage().getWei());
        }
        if (sourceEdge != null) {
            webBuilder.setSourceEdge(sourceEdge.getMessage().getWei());
        }
        return builder.setWeb(webBuilder).build();
    }
}
