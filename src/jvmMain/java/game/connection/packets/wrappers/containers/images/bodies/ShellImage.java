package game.connection.packets.wrappers.containers.images.bodies;

import game.connection.packets.messages.EdgeM;
import game.connection.packets.messages.ServerMessage;
import game.connection.packets.wrappers.containers.images.ArrayRealVectorImage;
import game.connection.packets.wrappers.containers.images.edges.EdgeImage;
import game.connection.packets.wrappers.containers.images.edges.WorldEdgeImage;
import game.world.Point;
import game.world.World;
import game.world.bodies.Shell;
import game.world.bodies.Turtle;
import game.world.bodies.Web;
import game.world.constraints.Edge;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.util.ArrayList;

import static game.util.DevConfig.doublePrecision;

public class ShellImage extends BodyImage<Shell> {
    @Serial
    private static final long serialVersionUID = 800857;
    Integer parent;
    boolean leaveParentFlag;
    public ArrayList<WorldEdgeImage> straps;

    public ShellImage(Shell shell) {
        super(shell);
    }

    public ShellImage(@NotNull ServerMessage.WorldM.BodyM message) {
        super(message);
        ServerMessage.WorldM.BodyM.ShellM shellMessage = message.getShell();
        parent = shellMessage.getParent();
        leaveParentFlag = shellMessage.getLeaveParentFlag();
        straps = new ArrayList<>();
        for (EdgeM.WorldEdgeM strap : shellMessage.getStrapsList()) {
            straps.add(new WorldEdgeImage(strap));
        }
    }

    @Override
    public void makeImage(Shell shell) {
        edges = getEdgesImage(shell);
        points = getPointsImage(shell);
        bound = new ArrayList<>();
        for (Web web : shell.bound) {
            bound.add(World.getBodies().indexOf(web));
        }
        straps = new ArrayList<>();
        parent = World.getBodies().contains(shell.parent) ? World.getBodies().indexOf(shell.parent) : -1;
        leaveParentFlag = shell.leaveParentFlag;
        for (Edge e : shell.straps) {
            straps.add(new WorldEdgeImage(e));
        }
    }

    @Override
    public Shell getIsolatedBody() {
        body = new Shell();
        addPoints(body);
        addEdges(body);
        (body).leaveParentFlag = leaveParentFlag;
        return body;
    }

    @Override
    public void connectBody() {
        if (parent != -1) {
            body.parent = (Turtle) World.getBodies().get(parent);
        }
        body.leaveParentFlag = leaveParentFlag;
        for (WorldEdgeImage e : straps) {
            body.straps.add(e.getEdge());
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
        //region add bound
        for (Integer binder : bound) {
            builder.addBound(binder);
        }
        //endregion

        ServerMessage.WorldM.BodyM.ShellM.Builder shellBuilder = ServerMessage.WorldM.BodyM.ShellM.newBuilder()
                .setParent(parent)
                .setLeaveParentFlag(leaveParentFlag);
        for (WorldEdgeImage strap : straps) {
            shellBuilder.addStraps(strap.getMessage().getWei());
        }
        builder.setShell(shellBuilder.build());
        return builder.build();
    }
}
