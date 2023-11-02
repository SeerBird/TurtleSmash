package game.world.bodies;

import game.Config;
import game.world.BPoint;
import game.world.CollisionData;
import game.world.World;
import game.world.constraints.Edge;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static game.util.Maths.*;

public class Shell extends Body {
    int size;
    Turtle parent;
    ArrayList<Edge> straps;
    boolean leaveParentFlag = true;

    public Shell(@NotNull ArrayRealVector pos, @Nullable Turtle parent) {
        super();
        straps = new ArrayList<>();
        this.parent = parent;
        form(pos.combine(1, 1, new ArrayRealVector(new Double[]{0.0, -25.0 * size})), Config.shellMass);
        //region if there is a parent, attach
        if (parent != null) {
            int i = 0;
            for (BPoint p : this.parent.getShellAttachment()) {
                straps.add(new Edge(p, points.get(i)));
                i++;
            }
            leaveParentFlag = false;
        }
        //endregion
    }

    @Override
    public boolean constrain() {
        {
            if (parent != null) {
                if (isFree()) {
                    if (leaveParentFlag) {
                        parent.abandonShell();
                        parent = null;
                    }
                    leaveParentFlag = true;
                }
            }
        }//try to leave parent if free (and stop being collisionless with it)
        boolean sat = super.constrain();
        boolean snap = false;
        for (Edge e : straps) {
            if (e.getExtension() > Config.shellStrapExtensionLimit) {
                snap = true;
                break;
            }
            sat &= e.satisfy();
        }
        if (snap) {
            straps.clear();
        }
        return sat;
    }

    @Override
    public void collide(@NotNull CollisionData collision) {
        Body b2 = collision.getEdge1().getParentBody();
        if (b2 != parent) {
            if (b2.getClass() == Shell.class && isFree()) {
                if (((Shell) b2).isFree() && collision.overlap.getNorm() > Config.shellMergeThreshold) {
                    double mass2 = b2.getMass();
                    double tot = mass2 + getMass();
                    form(getCenter().combine(getMass() / (tot), mass2 / (tot), b2.getCenter()), tot); //merge
                    ((Shell) b2).form(getCenter(), 0);
                    World.deleteBody(b2);
                    return;
                }
            }
            super.collide(collision);
        } else if (isFree()) {
            leaveParentFlag = false;
        }
    }

    @Override
    public boolean collides(@NotNull Body body) {
        if (body.getClass() != Web.class) {
            if (parent != null) {
                return body != parent;
            }
            return true;
        }
        return false;
    }

    private void form(@NotNull ArrayRealVector pos, double mass) {
        if (!isFree()) {
            logger.warning("Reforming a shell while it's still attached to a turtle. This doesn't make sense.");
        }
        points.clear();
        edges.clear();
        double size = Config.turtleSize / 6 * Math.pow(mass / Config.shellMass, 1 / 3.0);
        mass /= 8;
        BPoint p1 = new BPoint(this, mass, pos.getEntry(0) + 140 * size, pos.getEntry(1) + 275 * size);
        BPoint p2 = new BPoint(this, mass, pos.getEntry(0) + 200 * size, pos.getEntry(1) + 150 * size);
        BPoint p3 = new BPoint(this, mass, reflect(p2.getPos(), pos, i));
        BPoint p4 = new BPoint(this, mass, reflect(p1.getPos(), pos, i));
        BPoint p5 = new BPoint(this, mass, reflect(p4.getPos(), pos, j));
        BPoint p6 = new BPoint(this, mass, reflect(p3.getPos(), pos, j));
        BPoint p7 = new BPoint(this, mass, reflect(p2.getPos(), pos, j));
        BPoint p8 = new BPoint(this, mass, reflect(p1.getPos(), pos, j));
        addPoints(p2, p3, p6, p7, p1, p4, p5, p8);
        addEdgeChain(p1, p2, p3, p4, p5, p6, p7, p8, p1);
        //structure
        addEdge(p2, p7);
        addEdge(p3, p6);
        addEdge(p1, p4);
        addEdge(p8, p5);
        addEdge(p2, p6);
        addEdge(p7, p4);
    }

    public boolean isFree() {
        return straps.size() == 0;
    }

    private void snap() {

    }

    @Override
    public void gravitate(Body b) {
        if (b != parent) {
            super.gravitate(b);
        }
    }

    @Override
    public ArrayList<Edge> getSides() {
        ArrayList<Edge> sides = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            sides.add(edges.get(i));
        }
        return sides;
    }
}
