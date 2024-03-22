package game.world.bodies;

import game.GameHandler;
import game.connection.packets.wrappers.containers.images.animations.ShellSnapFlashAnimationImage;
import game.output.Renderer;
import game.output.animations.ShellSnapFlashAnimation;
import game.util.DevConfig;
import game.world.BPoint;
import game.world.CollisionData;
import game.world.World;
import game.world.constraints.Edge;
import game.world.constraints.FixedEdge;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static game.util.Maths.*;


public class Shell extends Body {
    public Turtle parent;
    public ArrayList<Edge> straps;
    public boolean leaveParentFlag = true;
    static int[] attachments = new int[]{0, 3, 4, 7, 5, 6, 1, 2};

    public Shell(@NotNull ArrayRealVector pos, ArrayRealVector heading, @Nullable Turtle parent) {
        super();
        straps = new ArrayList<>();
        this.parent = parent;
        form(pos.add(new ArrayRealVector(new Double[]{0.0, -5.0})), heading, DevConfig.shellMass);
        //region if there is a parent, attach
        if (parent != null) {
            for (int i = 0; i < attachments.length; i++) {
                straps.add(new FixedEdge(points.get(attachments[i]), parent.getShellAttachment().get(i % 4)));
            }
            leaveParentFlag = false;
            accelerate(parent.getVelocity());
        }
        //endregion
        World.addBody(this);
    }

    public Shell() {
        super();
        straps = new ArrayList<>();
        bound = new ArrayList<>();
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
        boolean sat = true;
        for (int i = 0; i < 6; i++) {
            sat &= super.constrain();
        }
        boolean snap = false;
        for (Edge e : straps) {
            if (e.getExtension() > DevConfig.shellStrapExtensionLimit) {
                snap = true;
                break;
            }
            sat &= e.satisfy();
        }
        if (snap) {
            straps.clear();
            if (GameHandler.isHost(parent.owner)) {
                Renderer.addAnimation(new ShellSnapFlashAnimation());
            } else {
                if (parent.owner != null) {
                    GameHandler.sendAnimation(parent.owner, new ShellSnapFlashAnimationImage());
                }
            }
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
                if (((Shell) b2).isUnbound() && collision.overlap.getNorm() > DevConfig.shellMergeThreshold) {
                    double mass1 = getMass();
                    double mass2 = b2.getMass();
                    double tot = mass1 + mass2;
                    ArrayRealVector velocity = getVelocity().combine(mass1 / tot, mass2 / tot, b2.getVelocity());
                    form(getCenter().combine(mass1 / tot, mass2 / tot, b2.getCenter()), randomUnitVector(), tot); //merge
                    b2.delete();
                    accelerate(velocity);
                    return;
                }
                //endregion
            }
            super.collide(collision); // not merging and not a parent
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

    private void form(@NotNull ArrayRealVector pos, ArrayRealVector j, double mass) {
        if (!isFree()) {
            logger.warning("Reforming a shell while it's still attached to a turtle. This doesn't make sense.");
        }
        points.clear();
        edges.clear();
        double size = DevConfig.turtleSize / 6 * Math.pow(mass / DevConfig.shellMass, 0.3333);
        mass /= 8;
        ArrayRealVector i = getVector(j.getEntry(1), -j.getEntry(0));
        BPoint p1 = new BPoint(this, mass, pos.add(i.combine(140 * size, 275 * size, j)));
        BPoint p2 = new BPoint(this, mass, pos.add(i.combine(200 * size, 150 * size, j)));
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
        addEdge(p2, p4);
        addEdge(p6, p8);
        addEdge(p3, p6);
        addEdge(p1, p4);
        addEdge(p8, p5);
        addEdge(p2, p6);
        addEdge(p7, p4);
        getCenter();
        velocity.set(0);
    }

    public boolean isFree() {
        return straps.isEmpty();
    }

    public boolean isUnbound() {
        return parent == null && bound.isEmpty();
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

    @Override
    public void delete() {
        super.delete();
        if (parent != null) {
            parent.abandonShell();
        }
    }
}
