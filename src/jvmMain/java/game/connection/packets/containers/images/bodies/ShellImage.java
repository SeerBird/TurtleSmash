package game.connection.packets.containers.images.bodies;

import game.connection.packets.containers.images.edges.WorldEdgeImage;
import game.world.World;
import game.world.bodies.Body;
import game.world.bodies.Shell;
import game.world.bodies.Turtle;
import game.world.bodies.Web;
import game.world.constraints.Edge;

import java.util.ArrayList;

public class ShellImage extends BodyImage {
    Integer parent;
    boolean leaveParentFlag;
    public ArrayList<WorldEdgeImage> straps;
    public ArrayList<Integer> bound;

    public ShellImage(Shell shell) {
        super(shell);
        straps = new ArrayList<>();
        bound = new ArrayList<>();
        parent = World.getBodies().indexOf(shell.parent);
        leaveParentFlag = shell.leaveParentFlag;
        for (Web web : shell.bound) {
            bound.add(World.getBodies().indexOf(web));
        }
        for (Edge e : shell.straps) {
            straps.add(new WorldEdgeImage(e));
        }
    }

    @Override
    public Shell getIsolatedBody() {
        this.body = new Shell();
        addPoints(body);
        addEdges(body);
        ((Shell) body).leaveParentFlag = leaveParentFlag;
        return (Shell) body; //check?
    }

    @Override
    public void connectBody() {
        Shell shell = (Shell) body;
        if (parent != null) {
            shell.parent = (Turtle) World.getBodies().get(parent);
        }
        shell.leaveParentFlag = leaveParentFlag;
        for (WorldEdgeImage e : straps) {
            shell.straps.add(e.getEdge());
        }
        for (Integer i : bound) {
            shell.bound.add((Web) World.getBodies().get(i));
        }
    }
}
