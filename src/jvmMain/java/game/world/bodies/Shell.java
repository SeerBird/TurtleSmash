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
    boolean leaveParentFlag = true;
    static int[] attachments = new int[]{0, 3, 4, 7};

    public Shell(@NotNull ArrayRealVector pos, @Nullable Turtle parent) {
        super();
        straps = new ArrayList<>();
        this.parent = parent;
        for(int i=0;i<8;i++){
            addPoint(0, Maths.o);
        }
        for(int i=0;i<8;i++){
            addEdge(points.get(i),points.get((i+1)%8));
        }
        //region structure
        addEdge(points.get(1), points.get(6));
        addEdge(points.get(2), points.get(5));
        addEdge(points.get(0), points.get(3));
        addEdge(points.get(7), points.get(4));
        addEdge(points.get(1), points.get(5));
        addEdge(points.get(6), points.get(3));
        //endregion
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
            if (b2.getClass() == Shell.class && isFree()) {
                //region merge if collision strong enough
                if (((Shell) b2).isFree() && collision.overlap.getNorm() > Config.shellMergeThreshold) {
                    double mass1 = getMass();
                    double mass2 = b2.getMass();
                    double tot = mass1 + mass2;
                    ArrayRealVector velocity = getVelocity().combine(mass1 / tot, mass2 / tot, b2.getVelocity());
                    form(getCenter().combine(mass1 / tot, mass2 / tot, b2.getCenter()), tot); //merge
                    World.deleteBody(b2);
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
        double size = Config.turtleSize / 6 * Math.pow(mass / Config.shellMass, 0.3333);
        mass /= 8;
        ArrayList<ArrayRealVector> positions = new ArrayList<>();
        positions.add(pos.add(getVector(140 * size, 278 * size)));
        positions.add(pos.add(getVector(200 * size, 150 * size)));
        positions.add(reflect(positions.get(1), pos, i));
        positions.add(reflect(positions.get(0), pos, i));
        positions.add(reflect(positions.get(3), pos, j));
        positions.add(reflect(positions.get(2), pos, j));
        positions.add(reflect(positions.get(1), pos, j));
        positions.add(reflect(positions.get(0), pos, j));
        BPoint p;
        for (int i = 0; i < 8; i++) {
            p = points.get(i);
            p.setPos(positions.get(i));
            p.stop();
            p.setMass(mass);
        }
        for(Edge e: edges){
            e.resetRest();
        }
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
