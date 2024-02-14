package game.connection.packets.containers.images.bodies;

import game.GameHandler;
import game.world.BPoint;
import game.world.World;
import game.world.bodies.Shell;
import game.world.bodies.Turtle;
import game.world.bodies.Web;

import java.io.Serial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TurtleImage extends BodyImage<Turtle> {
    @Serial
    private static final long serialVersionUID = 800858;
    Map<Integer, Integer> spinnerets;
    Integer shell;
    Integer owner;

    public TurtleImage(Turtle turtle) {
        super(turtle);
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
                    World.getBodies().contains(turtle.spinnerets.get(p)) ? World.getBodies().indexOf(turtle.spinnerets.get(p)) : null);
        }
        shell = World.getBodies().contains(turtle.shell) ? World.getBodies().indexOf(turtle.shell) : null;
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
                    spinnerets.get(i) == null ? null : (Web) World.getBodies().get(spinnerets.get(i)));
        }
        body.owner = owner == -1 ? null : GameHandler.getLocalPlayerFromServerId(owner);
        body.shell = shell == null ? null : (Shell) World.getBodies().get(shell);
    }
}
