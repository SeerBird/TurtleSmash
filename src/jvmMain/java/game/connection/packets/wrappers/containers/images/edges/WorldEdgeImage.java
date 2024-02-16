package game.connection.packets.wrappers.containers.images.edges;

import game.connection.packets.messages.EdgeM;
import game.world.World;
import game.world.bodies.Body;
import game.world.constraints.Edge;
import game.world.constraints.FixedEdge;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;

import static game.util.DevConfig.doublePrecision;

public class WorldEdgeImage extends EdgeImage {
    @Serial
    private static final long serialVersionUID = 8008504;
    public int bi1;
    public int bi2;
    public int i1;
    public int i2;
    public float d;

    public WorldEdgeImage(@NotNull Edge e) {
        Body parent = e.getEdge1().getParentBody();
        bi1 = World.getBodies().indexOf(parent);
        i1 = parent.getPoints().indexOf(e.getEdge1());
        parent = e.getEdge2().getParentBody();
        bi2 = World.getBodies().indexOf(parent);
        i2 = parent.getPoints().indexOf(e.getEdge2());
        d = (float) e.getRestDistance();
    }

    public WorldEdgeImage(@NotNull EdgeM.WorldEdgeM message) {
        bi1 = message.getBi1();
        bi2 = message.getBi2();
        i1 = message.getI1();
        i2 = message.getI2();
        d = (float) (message.getD() / doublePrecision);
    }


    public Edge getEdge() {
        if (bi1 == -1 || bi2 == -1 || i1 == -1 || i2 == -1) {
            return null;
        }
        return new FixedEdge(World.getBodies().get(bi1).getPoints().get(i1),
                World.getBodies().get(bi2).getPoints().get(i2), d);
    }

    @Override
    public EdgeM getMessage() {
        return EdgeM.newBuilder()
                .setWei(EdgeM.WorldEdgeM.newBuilder()
                        .setBi1(bi1)
                        .setI1(i1)
                        .setBi2(bi2)
                        .setI2(i2)
                        .setD((int) (d * doublePrecision)))
                .build();
    }
}
