package game.connection.packets.wrappers.containers.images.bodies;

import game.GameHandler;
import game.connection.packets.messages.ServerMessage;
import game.connection.packets.wrappers.containers.images.ArrayRealVectorImage;
import game.connection.packets.wrappers.containers.images.edges.EdgeImage;
import game.world.BPoint;
import game.world.Point;
import game.world.World;
import game.world.bodies.Shell;
import game.world.bodies.Turtle;
import game.world.bodies.Web;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.io.Serial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static game.util.DevConfig.doublePrecision;

public class TurtleImage extends BodyImage<Turtle> {
    @Serial
    private static final long serialVersionUID = 800858;
    Map<Integer, Integer> spinnerets;
    Integer shell;
    Integer owner;

    public TurtleImage(Turtle turtle) {
        super(turtle);
    }

    public TurtleImage(ServerMessage.WorldM.BodyM message) {
        super(message);
        ServerMessage.WorldM.BodyM.TurtleM turtleMessage = message.getTurtle();
        spinnerets = turtleMessage.getSpinneretsMap();
        shell = turtleMessage.getShell();
        owner = turtleMessage.getOwner();
    }

    @Override
    public void makeImage(Turtle turtle) {
        edges = getEdgesImage(turtle);
        points = getPointsImage(turtle);
        bound = new ArrayList<>();
        for (Web web : turtle.bound) {
            bound.add(World.getBodies().indexOf(web));
        }
        spinnerets = new HashMap<>();
        for (BPoint p : turtle.spinnerets.keySet()) {
            spinnerets.put(turtle.getPoints().indexOf(p),
                    World.getBodies().contains(turtle.spinnerets.get(p)) ? World.getBodies().indexOf(turtle.spinnerets.get(p)) : -1);
        }
        shell = World.getBodies().contains(turtle.shell) ? World.getBodies().indexOf(turtle.shell) : -1;
        owner = GameHandler.getPlayers().indexOf(turtle.owner);
    }

    @Override
    public Turtle getIsolatedBody() {
        body = new Turtle();
        addPoints(body);
        addEdges(body);
        return body; //check?
    }

    @Override
    public void connectBody() {
        for (Integer i : spinnerets.keySet()) {
            body.spinnerets.put(body.getPoints().get(i),
                    spinnerets.get(i) == -1 ? null : (Web) World.getBodies().get(spinnerets.get(i)));
        }
        body.owner = owner == -1 ? null : GameHandler.getLocalPlayerFromServerId(owner);
        body.shell = shell == -1 ? null : (Shell) World.getBodies().get(shell);
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
        ServerMessage.WorldM.BodyM.TurtleM.Builder turtleBuilder = ServerMessage.WorldM.BodyM.TurtleM.newBuilder()
                .setOwner(owner)
                .setShell(shell);
        for (Integer spinneret : spinnerets.keySet()) {
            turtleBuilder.putSpinnerets(spinneret, spinnerets.get(spinneret));
        }
        return builder.setTurtle(turtleBuilder.build()).build();
    }
}
