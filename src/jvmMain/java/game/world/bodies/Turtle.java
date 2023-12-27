package game.world.bodies;

import game.util.DevConfig;
import game.Player;
import game.output.audio.Audio;
import game.output.audio.Sound;
import game.world.BPoint;
import game.world.CollisionData;
import game.world.World;
import game.world.constraints.Edge;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static game.util.DevConfig.turtleMass;
import static game.util.Maths.*;

public class Turtle extends Body {
    public Map<BPoint, Web> spinnerets;
    public Shell shell;
    public int nakedFrames;
    public Player owner;
    static ArrayList<Integer> shellAttachment = new ArrayList<>(Arrays.asList(7, 8, 14, 15));

    /**
     * @param pos   the position of the center of the main rectangle of the turtle body
     * @param owner the player object that this turtle object will issue death events to
     */
    public Turtle(@NotNull ArrayRealVector pos, @Nullable Player owner) {
        super();
        this.owner = owner;
        spinnerets = new HashMap<>();
        //region shape controls
        double length = 500;
        double width = 330;
        ArrayRealVector arm = new ArrayRealVector(new Double[]{55.0, 35.0});
        double awidth = 60;
        ArrayRealVector leg = new ArrayRealVector(new Double[]{55.0, -35.0});
        double lwidth = 60;
        double hwidth = 60;
        double hlength = 60;
        double tlength = 70;
        double twidth = 20;
        //endregion
        //region scaling
        double size = DevConfig.turtleSize / 6;
        length *= size;
        width *= size;
        awidth *= size;
        lwidth *= size;
        hwidth *= size;
        hlength *= size;
        tlength *= size;
        twidth *= size; //this is for testing only, don't leave this here
        arm.mapMultiplyToSelf(size);
        leg.mapMultiplyToSelf(size);
        //endregion
        //region head
        ArrayRealVector temp = pos.combine(1, length / 2 + hlength, j).combine(1, hwidth / 2, i);
        BPoint head3 = new BPoint(this, turtleMass, temp);
        BPoint head4 = new BPoint(this, turtleMass, temp.combine(1, -hlength, j));
        BPoint head2 = new BPoint(this, turtleMass, temp.combine(1, -hwidth, i));
        BPoint head1 = new BPoint(this, turtleMass, temp.combine(1, -hlength, j).combine(1, -hwidth, i));
        //endregion
        //region right arm
        temp = pos.combine(1, length / 2, j).combine(1, width / 2, i);
        double norm = arm.getNorm();
        BPoint rightArm1 = new BPoint(this, turtleMass, temp.combine(1, -awidth * arm.getEntry(1) / norm, i));
        BPoint rightArm4 = new BPoint(this, turtleMass, temp.combine(1, -awidth * arm.getEntry(0) / norm, j));
        BPoint rightArm2 = new BPoint(this, turtleMass, rightArm1.getPos().add(arm));
        BPoint rightArm3 = new BPoint(this, turtleMass, rightArm4.getPos().add(arm));
        //endregion
        //region left arm
        BPoint leftArm4 = new BPoint(this, turtleMass, reflect(rightArm1.getPos(), pos, j));
        BPoint leftArm1 = new BPoint(this, turtleMass, reflect(rightArm4.getPos(), pos, j));
        BPoint leftArm3 = new BPoint(this, turtleMass, reflect(rightArm2.getPos(), pos, j));
        BPoint leftArm2 = new BPoint(this, turtleMass, reflect(rightArm3.getPos(), pos, j));
        //endregion
        //region right leg
        temp = pos.combine(1, -length / 2, j).combine(1, width / 2, i);
        norm = leg.getNorm();
        BPoint rightLeg4 = new BPoint(this, turtleMass, temp.combine(1, lwidth * leg.getEntry(1) / norm, i));
        BPoint rightLeg1 = new BPoint(this, turtleMass, temp.combine(1, lwidth * leg.getEntry(0) / norm, j));
        BPoint rightLeg3 = new BPoint(this, turtleMass, rightLeg4.getPos().add(leg));
        BPoint rightLeg2 = new BPoint(this, turtleMass, rightLeg1.getPos().add(leg));
        //endregion
        //region left leg
        BPoint leftLeg1 = new BPoint(this, turtleMass, reflect(rightLeg4.getPos(), pos, j));
        BPoint leftLeg4 = new BPoint(this, turtleMass, reflect(rightLeg1.getPos(), pos, j));
        BPoint leftLeg2 = new BPoint(this, turtleMass, reflect(rightLeg3.getPos(), pos, j));
        BPoint leftLeg3 = new BPoint(this, turtleMass, reflect(rightLeg2.getPos(), pos, j));
        //endregion
        //region tail
        BPoint tail2 = new BPoint(this, turtleMass, pos.combine(1, -length / 2 - tlength, j));
        BPoint tail1 = new BPoint(this, turtleMass, pos.combine(1, -length / 2, j).combine(1, twidth / 2, i));
        BPoint tail3 = new BPoint(this, turtleMass, reflect(tail1.getPos(), pos, j));
        //endregion
        //region add all the points to their sets, create the side chain(clockwise)
        addPoints(head1, head2, head3, head4,
                rightArm1, rightArm2, rightArm3, rightArm4,
                rightLeg1, rightLeg2, rightLeg3, rightLeg4,
                tail1, tail2, tail3,
                leftLeg1, leftLeg2, leftLeg3, leftLeg4,
                leftArm1, leftArm2, leftArm3, leftArm4);
        addEdgeChain(head1, head2, head3, head4,
                rightArm1, rightArm2, rightArm3, rightArm4,
                rightLeg1, rightLeg2, rightLeg3, rightLeg4,
                tail1, tail2, tail3,
                leftLeg1, leftLeg2, leftLeg3, leftLeg4,
                leftArm1, leftArm2, leftArm3, leftArm4,
                head1);
        for (BPoint p : Arrays.asList(rightArm2, leftArm3, rightLeg2, leftLeg3)) {
            spinnerets.put(p, null);
        }
        //endregion
        //region internal structure
        addEdge(rightArm4, leftLeg4);
        addEdge(leftArm1, rightLeg1);
        addEdge(rightArm1, rightArm3);
        addEdge(rightArm4, rightArm2);
        addEdge(leftArm4, leftArm2);
        addEdge(leftArm1, leftArm3);
        addEdge(leftLeg1, leftLeg3);
        addEdge(leftLeg4, leftLeg2);
        addEdge(rightLeg4, rightLeg2);
        addEdge(rightLeg1, rightLeg3);
        addEdge(head3, head1);
        addEdge(head4, head2);
        addEdge(rightArm1, leftArm1);
        addEdge(leftArm4, rightArm4);
        addEdge(rightLeg1, tail1);
        addEdge(leftLeg4, tail3);
        addEdge(head4, leftArm1);
        addEdge(head1, rightArm4);
        addEdge(rightArm4, rightLeg4);
        addEdge(leftArm1, leftLeg1);
        addEdge(rightLeg1, rightArm1);
        addEdge(leftLeg4, leftArm4);
        addEdge(tail1, tail3);
        addEdge(leftLeg4, tail1);
        addEdge(rightLeg1, tail3);
        addEdge(head1, tail1);
        addEdge(head4, tail3);
        //endregion
        World.addBody(this);
        growShell();
    }

    public Turtle() {
        super();
        spinnerets = new HashMap<>();
    }

    @Override
    public void move() {
        super.move();
        if (shell == null) {
            nakedFrames--;
            if (nakedFrames < 0) {
                growShell();
            }
        }
    }

    @Override
    public ArrayList<Edge> getSides() {
        ArrayList<Edge> sides = new ArrayList<>();
        for (int i = 0; i < 23; i++) {
            sides.add(edges.get(i));
        }
        return sides;
    }

    public void webFling(ArrayRealVector pos) {
        double minNorm = Double.MAX_VALUE;
        ArrayRealVector minDist = new ArrayRealVector(2);
        BPoint spinneret = null;
        //region find nearest spinneret
        ArrayRealVector compareDist;
        double compareNorm;
        for (BPoint p : spinnerets.keySet()) {
            if (spinnerets.get(p) == null) {
                compareDist = p.getDistance(pos);
                compareNorm = compareDist.getNorm();
                if (compareNorm < minNorm) {
                    minNorm = compareNorm;
                    minDist = compareDist;
                    spinneret = p;
                }
            }
        }
        //endregion
        if (spinneret != null) {
            minDist.mapMultiplyToSelf(DevConfig.webFling / minNorm);//make the vector size the configured velocity
            spinnerets.put(spinneret, new Web(spinneret, minDist.add(spinneret.getVelocity())));//FLING and record it
            ArrayRealVector recoil = (ArrayRealVector) minDist.add(spinneret.getVelocity()).mapMultiply(-DevConfig.recoil / mass);
            spinneret.accelerate(recoil);
            accelerate(recoil);
        }
    }

    public void detachWeb(ArrayRealVector mousepos) {
        double minNorm = Double.MAX_VALUE;
        BPoint spinneret = null;
        //region find nearest spinneret
        ArrayRealVector compareDist;
        double compareNorm;
        for (BPoint p : spinnerets.keySet()) {
            if (spinnerets.get(p) != null) {
                compareDist = p.getDistance(mousepos);
                compareNorm = compareDist.getNorm();
                if (compareNorm < minNorm) {
                    minNorm = compareNorm;
                    spinneret = p;
                }
            }
        }
        //endregion
        if (spinneret != null) {
            spinnerets.get(spinneret).disconnect();
            spinnerets.put(spinneret, null);
        }
    }

    ArrayList<BPoint> getShellAttachment() {
        ArrayList<BPoint> attachments = new ArrayList<>();
        for (int i : shellAttachment) {
            attachments.add(points.get(i));
        }
        return attachments;
    }

    @Override
    public void collide(@NotNull CollisionData collision) {
        Body b2 = collision.getEdge1().getParentBody();
        if (b2 == shell) {
            return;
        }
        super.collide(collision);
    }

    @Override
    public boolean collides(@NotNull Body body) {
        return body != shell && body.getClass() != Web.class;
    }

    @Override
    public void gravitate(Body b) {
        if (b != shell) {
            super.gravitate(b);
        }
    }

    @Override
    public boolean constrain() {
        boolean satisfied = true;
        for (int i = 0; i < 6; i++) {
            for (Edge c : edges) {
                if (isAlive()) {
                    if (c.getExtension() > DevConfig.turtleDeformThreshold) {
                        die();
                    }
                }
                satisfied &= c.satisfy();
            }
        }
        return satisfied;
    }

    private void die() {
        owner.die(); //let the player know they died
        Audio.playSound(Sound.death);
        owner = null; //become a lifeless remnant of what once used to be
    }

    public boolean isAlive() {
        return owner != null;
    }

    public void growShell() {
        this.shell = new Shell(getCenter(), getHeading(), this);
    }

    public ArrayRealVector getHeading() {
        return normalize(points.get(13).getDistance(points.get(1).getPos().combine(0.5, 0.5, points.get(2).getPos())));
    }

    public void abandonShell() {
        shell = null;
        nakedFrames = DevConfig.turtleNakedFrames;
    }

    @Override
    public void delete() {
        if (shell == null) {
            super.delete();
            if (isAlive()) {
                die();
            }
        }
    }
}
