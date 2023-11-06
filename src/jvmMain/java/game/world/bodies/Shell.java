package game.world.bodies;

import game.Config;
import game.util.Maths;
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
    public ArrayList<Edge> straps;
    ArrayList<Web> bound;
    boolean leaveParentFlag = true;
    static int[] attachments = new int[]{0, 3, 4, 7};

    public Shell(@NotNull ArrayRealVector pos, @Nullable Turtle parent) {
        super();
        straps = new ArrayList<>();
        bound = new ArrayList<>();
        this.parent = parent;
        form(pos.add(new ArrayRealVector(new Double[]{0.0, -5.0})), Config.shellMass);
        //region if there is a parent, attach
        if (parent != null) {
            for (int i = 0; i < attachments.length; i++) {
                straps.add(new Edge(points.get(attachments[i]), parent.getShellAttachment().get(i)));
            }
            leaveParentFlag = false;
        }
        //endregion
    }

    @Override
    public boolean constrain() {
        //region try to leave parent if free (and stop being collisionless with it)
        if (parent != null) {
            if (isFree()) {
                if (leaveParentFlag) {
                    parent.abandonShell();
                    parent = null;
                }
                leaveParentFlag = true;
            }
        }
        //endregion
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
        Body b2;
        if (collision.getVertex().getParentBody() == this) {
            b2 = collision.getEdge1().getParentBody();
        } else {
            b2 = collision.getVertex().getParentBody();
        }
        if (b2 != parent) {
            if (b2.getClass() == Shell.class && isUnbound()) {
                //region merge if collision strong enough
                if (((Shell) b2).isUnbound() && collision.overlap.getNorm() > Config.shellMergeThreshold) {
                    double mass1 = getMass();
                    double mass2 = b2.getMass();
                    double tot = mass1 + mass2;
                    ArrayRealVector velocity = getVelocity().combine(mass1 / tot, mass2 / tot, b2.getVelocity());
                    form(getCenter().combine(mass1 / tot, mass2 / tot, b2.getCenter()), tot); //merge
                    World.removeBody(b2);
                    accelerate(velocity);
                    return;
                }
                //endregion
            }
            super.collide(collision); // a random body
        } else if (isFree()) {
            leaveParentFlag = false; // we haven't left the parent
        }
    }

    @Override
    public boolean collides(@NotNull Body body) {
        //region collides if not web and if body isn't parent or if in the process of leaving parent(to check if left)
        return (body.getClass() != Web.class) && (body != parent || (isFree() && parent != null));
        //endregion
    }

    private void form(@NotNull ArrayRealVector pos, double mass) {
        if (!isFree()) {
            logger.warning("Reforming a shell while it's still attached to a turtle. This doesn't make sense.");
        }
        points.clear();
        edges.clear();
        double size = Config.turtleSize / 6 * Math.pow(mass / Config.shellMass, 0.3333);
        mass /= 8;
        BPoint p1 = new BPoint(this, mass, pos.getEntry(0) + 140 * size, pos.getEntry(1) + 275 * size);
        BPoint p2 = new BPoint(this, mass, pos.getEntry(0) + 200 * size, pos.getEntry(1) + 150 * size);
        BPoint p3 = new BPoint(this, mass, reflect(p2.getPos(), pos, i));
        BPoint p4 = new BPoint(this, mass, reflect(p1.getPos(), pos, i));
        BPoint p5 = new BPoint(this, mass, reflect(p4.getPos(), pos, j));
        BPoint p6 = new BPoint(this, mass, reflect(p3.getPos(), pos, j));
        BPoint p7 = new BPoint(this, mass, reflect(p2.getPos(), pos, j));
        BPoint p8 = new BPoint(this, mass, reflect(p1.getPos(), pos, j));
        addPoints(p1, p2, p3, p4, p5, p6, p7, p8);
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
    public boolean isUnbound(){
        return straps.size() == 0&&bound.size()==0;
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

    public void addBinder(Web web) {
        bound.add(web);
    }
    public void unbind(Web web) {
        bound.remove(web);
    }
}
