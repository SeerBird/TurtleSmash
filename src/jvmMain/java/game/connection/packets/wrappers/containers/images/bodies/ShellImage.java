package game.connection.packets.wrappers.containers.images.bodies;

import game.connection.packets.wrappers.containers.images.edges.WorldEdgeImage;
import game.world.World;
import game.world.bodies.Shell;
import game.world.bodies.Turtle;
import game.world.bodies.Web;
import game.world.constraints.Edge;

import java.io.Serial;
import java.util.ArrayList;

public class ShellImage extends BodyImage<Shell> {
    @Serial
    private static final long serialVersionUID = 800857;
    Integer parent;
    boolean flag;
    public ArrayList<WorldEdgeImage> straps;

    public ShellImage(Shell shell) {
        super(shell);

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
        parent = World.getBodies().contains(shell.parent) ? World.getBodies().indexOf(shell.parent) : null;
        flag = shell.leaveParentFlag;
        for (Edge e : shell.straps) {
            straps.add(new WorldEdgeImage(e));
        }
    }

    @Override
    public Shell getIsolatedBody() {
        body = new Shell();
        addPoints(body);
        addEdges(body);
        (body).leaveParentFlag = flag;
        return body;
    }

    @Override
    public void connectBody() {
        if (parent != null) {
            body.parent = (Turtle) World.getBodies().get(parent);
        }
        body.leaveParentFlag = flag;
        for (WorldEdgeImage e : straps) {
            body.straps.add(e.getEdge());
        }
    }
}
