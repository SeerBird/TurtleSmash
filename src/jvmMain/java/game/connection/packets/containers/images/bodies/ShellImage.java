package game.connection.packets.containers.images.bodies;

import game.connection.packets.containers.images.edges.WorldEdgeImage;
import game.world.World;
import game.world.bodies.Shell;
import game.world.bodies.Turtle;
import game.world.constraints.Edge;

import java.util.ArrayList;

public class ShellImage extends BodyImage<Shell> {
    Integer parent;
    boolean flag;
    public ArrayList<WorldEdgeImage> straps;

    public ShellImage(Shell shell) {
        super(shell);
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
