package game.connection.packets.wrappers.containers.images.edges;

import game.connection.packets.messages.EdgeM;
import game.world.BPoint;
import game.world.bodies.Body;
import game.world.constraints.Edge;
import game.world.constraints.FixedEdge;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.util.ArrayList;

import static game.util.DevConfig.doublePrecision;

public class FixedEdgeImage extends EdgeImage {
    @Serial
    private static final long serialVersionUID = 8008503;
    public int i1;
    public int i2;
    public float d;

    public FixedEdgeImage(@NotNull Edge e) {
        ArrayList<BPoint> points = e.getEdge1().getParentBody().getPoints();
        i1 = points.indexOf(e.getEdge1());
        i2 = points.indexOf(e.getEdge2());
        d = (float) e.getRestDistance();
    }

    public FixedEdgeImage(EdgeM.FixedEdgeM message) {
        i1 = message.getI1();
        i2 = message.getI2();
        d = (float) (message.getD() / doublePrecision);
    }

    public Edge getEdge(@NotNull Body body) {
        return new FixedEdge(body.getPoints().get(i1), body.getPoints().get(i2), d);
    }

    @Override
    public EdgeM getMessage() {
        return EdgeM.newBuilder()
                .setFep(EdgeM.FixedEdgeM.newBuilder()
                        .setI1(i1)
                        .setI2(i2)
                        .setD((int) (d * doublePrecision)))
                .build();
    }
}
