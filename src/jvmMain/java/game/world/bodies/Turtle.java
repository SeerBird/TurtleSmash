package game.world.bodies;

import game.Config;
import game.world.BPoint;
import game.world.CollisionData;
import game.world.World;
import game.world.constraints.Edge;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import static game.util.Maths.*;

public class Turtle extends Body {
    ArrayList<BPoint> spinnerets;
    ArrayList<BPoint> shellAttachment;
    Shell shell;

    public Turtle(@NotNull ArrayRealVector pos) {
        super();
        spinnerets = new ArrayList<>();
        shellAttachment = new ArrayList<>();
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
        double size = Config.turtleSize / 6;
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
        //head
        ArrayRealVector temp = pos.combine(1, length / 2 + hlength, j).combine(1, hwidth / 2, i);
        BPoint head3 = new BPoint(this, 2, temp);
        BPoint head4 = new BPoint(this, 2, temp.combine(1, -hlength, j));
        BPoint head2 = new BPoint(this, 2, temp.combine(1, -hwidth, i));
        BPoint head1 = new BPoint(this, 2, temp.combine(1, -hlength, j).combine(1, -hwidth, i));
        //right arm
        temp = pos.combine(1, length / 2, j).combine(1, width / 2, i);
        double norm = arm.getNorm();
        BPoint rightArm1 = new BPoint(this, 2, temp.combine(1, -awidth * arm.getEntry(1) / norm, i));
        BPoint rightArm4 = new BPoint(this, 2, temp.combine(1, -awidth * arm.getEntry(0) / norm, j));
        BPoint rightArm2 = new BPoint(this, 2, rightArm1.getPos().add(arm));
        BPoint rightArm3 = new BPoint(this, 2, rightArm4.getPos().add(arm));
        //left arm
        BPoint leftArm4 = new BPoint(this, 2, reflect(rightArm1.getPos(), pos, j));
        BPoint leftArm1 = new BPoint(this, 2, reflect(rightArm4.getPos(), pos, j));
        BPoint leftArm3 = new BPoint(this, 2, reflect(rightArm2.getPos(), pos, j));
        BPoint leftArm2 = new BPoint(this, 2, reflect(rightArm3.getPos(), pos, j));
        //right leg
        temp = pos.combine(1, -length / 2, j).combine(1, width / 2, i);
        norm = leg.getNorm();
        BPoint rightLeg4 = new BPoint(this, 2, temp.combine(1, lwidth * leg.getEntry(1) / norm, i));
        BPoint rightLeg1 = new BPoint(this, 2, temp.combine(1, lwidth * leg.getEntry(0) / norm, j));
        BPoint rightLeg3 = new BPoint(this, 2, rightLeg4.getPos().add(leg));
        BPoint rightLeg2 = new BPoint(this, 2, rightLeg1.getPos().add(leg));
        //left leg
        BPoint leftLeg1 = new BPoint(this, 2, reflect(rightLeg4.getPos(), pos, j));
        BPoint leftLeg4 = new BPoint(this, 2, reflect(rightLeg1.getPos(), pos, j));
        BPoint leftLeg2 = new BPoint(this, 2, reflect(rightLeg3.getPos(), pos, j));
        BPoint leftLeg3 = new BPoint(this, 2, reflect(rightLeg2.getPos(), pos, j));
        //tail
        BPoint tail2 = new BPoint(this, 2, pos.combine(1, -length / 2 - tlength, j));
        BPoint tail1 = new BPoint(this, 2, pos.combine(1, -length / 2, j).combine(1, twidth / 2, i));
        BPoint tail3 = new BPoint(this, 2, reflect(tail1.getPos(), pos, j));
        //add
        addPoints(head3, head4, head2, head1, rightArm1, rightArm4, rightArm2, rightArm3, leftArm4, leftArm1, leftArm3, leftArm2, rightLeg4, rightLeg1, rightLeg3, rightLeg2, leftLeg1, leftLeg4, leftLeg2, leftLeg3, tail2, tail1, tail3);
        addEdgeChain(head1, head2, head3, head4,
                rightArm1, rightArm2, rightArm3, rightArm4,
                rightLeg1, rightLeg2, rightLeg3, rightLeg4,
                tail1, tail2, tail3,
                leftLeg1, leftLeg2, leftLeg3, leftLeg4,
                leftArm1, leftArm2, leftArm3, leftArm4,
                head1);
        spinnerets.addAll(new ArrayList<>(Arrays.asList(rightArm2, leftArm3, rightLeg2, leftLeg3)));
        shellAttachment.addAll(new ArrayList<>(Arrays.asList(rightArm1, rightLeg1, leftLeg1, leftArm1)));
        //structure
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
        growShell();
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
        ArrayRealVector dist = spinnerets.get(0).getDistance(pos);
        double minNorm = dist.getNorm();
        int spinneretID = 0;
        ArrayRealVector compareDist;
        double compareNorm;
        for (int i = 1; i < spinnerets.size(); i++) {
            compareDist = spinnerets.get(i).getDistance(pos);
            compareNorm = compareDist.getNorm();
            if (compareNorm < minNorm) {
                minNorm = compareNorm;
                dist = compareDist;
                spinneretID = i;
            }
        }//find closest spinneret
        dist.mapMultiplyToSelf(Config.stringFling / minNorm);//set vector to the configured velocity
        new Web(spinnerets.get(spinneretID), dist.add(spinnerets.get(spinneretID).getVelocity()));//FLING
    }

    ArrayList<BPoint> getShellAttachment() {
        return shellAttachment;
    }

    public void growShell() {
        this.shell = new Shell(getCenter(), this);
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
    public void gravitate(Body b) {
        if (b != shell) {
            super.gravitate(b);
        }
    }

    public void abandonShell() {
        shell = null;
    }
}
