package game.connection.packets.containers.images.bodies;

import game.GameHandler;
import game.connection.packets.containers.images.bodies.BodyImage;
import game.world.BPoint;
import game.world.World;
import game.world.bodies.Body;
import game.world.bodies.Shell;
import game.world.bodies.Turtle;
import game.world.bodies.Web;

import java.util.HashMap;
import java.util.Map;

public class TurtleImage extends BodyImage {
    Map<Integer, Integer> spinnerets;
    Integer shell;
    Integer owner;

    public TurtleImage(Turtle turtle) {
        super(turtle);
        spinnerets = new HashMap<>();
        for (BPoint p : turtle.spinnerets.keySet()) {
            spinnerets.put(turtle.getPoints().indexOf(p),
                    turtle.spinnerets.get(p) == null ? null : World.getBodies().indexOf(turtle.spinnerets.get(p)));
        }
        shell = turtle.shell == null ? null : World.getBodies().indexOf(turtle.shell);
        owner = GameHandler.getPlayers().indexOf(turtle.owner);
    }

    @Override
    public Turtle getIsolatedBody() {
        body = new Turtle();
        addPoints(body);
        addEdges(body);
        return (Turtle) body; //check?
    }

    @Override
    public void connectBody() {
        Turtle turtle = (Turtle) body;
        for (Integer i : spinnerets.keySet()) {
            turtle.spinnerets.put(turtle.getPoints().get(i),
                    spinnerets.get(i) == null ? null : (Web) World.getBodies().get(spinnerets.get(i)));
        }
        turtle.owner = GameHandler.getLocalPlayerFromServerId(owner);
        turtle.shell = shell == null ? null : (Shell) World.getBodies().get(shell);
    }
}
