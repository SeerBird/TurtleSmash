package game.world.bodies;

import game.Config;
import game.world.VPoint;
import game.world.World;
import game.world.constraints.Edge;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

import static game.util.Maths.*;

public class Turtle extends Body {
    ArrayList<VPoint> spinnerets;

    public Turtle(@NotNull ArrayRealVector pos, ArrayRealVector side1, ArrayRealVector side2) {
        super();
        spinnerets = new ArrayList<>();
        double length = 500;
        double width = 330;
        ArrayRealVector arm = new ArrayRealVector(new Double[]{55.0, 35.0});
        double awidth = 60;
        ArrayRealVector leg = new ArrayRealVector(new Double[]{55.0, -35.0});
        double lwidth = 60;
        double hwidth = 60;
        double hlength = 60;
        double tlength = 40;
        double twidth = 20;
        double scalar = 1.0;
        length*=scalar;
        width*=scalar;
        awidth*=scalar;
        lwidth*=scalar;
        hwidth*=scalar;
        hlength*=scalar;
        tlength*=scalar;
        twidth*=scalar; //this is for testing only, don't leave this here
        //head
        ArrayRealVector temp = pos.combine(1, length / 2 + hlength, j).combine(1, hwidth / 2, i);
        VPoint head3 = new VPoint(this, 2, temp);
        VPoint head4 = new VPoint(this, 2, temp.combine(1, -hlength, j));
        VPoint head2 = new VPoint(this, 2, temp.combine(1, -hwidth, i));
        VPoint head1 = new VPoint(this, 2, temp.combine(1, -hlength, j).combine(1, -hwidth, i));
        //right arm
        temp = pos.combine(1, length / 2, j).combine(1, width / 2, i);
        double norm = arm.getNorm();
        VPoint rightArm1 = new VPoint(this, 2, temp.combine(1, -awidth * arm.getEntry(1) / norm, i));
        VPoint rightArm4 = new VPoint(this, 2, temp.combine(1, -awidth * arm.getEntry(0) / norm, j));
        VPoint rightArm2 = new VPoint(this, 2, rightArm1.getPos().add(arm));
        VPoint rightArm3 = new VPoint(this, 2, rightArm4.getPos().add(arm));
        //left arm
        VPoint leftArm4 = new VPoint(this, 2, reflect(rightArm1.getPos(), pos, j));
        VPoint leftArm1 = new VPoint(this, 2, reflect(rightArm4.getPos(), pos, j));
        VPoint leftArm3 = new VPoint(this, 2, reflect(rightArm2.getPos(), pos, j));
        VPoint leftArm2 = new VPoint(this, 2, reflect(rightArm3.getPos(), pos, j));
        //right leg
        temp = pos.combine(1, -length / 2, j).combine(1, width / 2, i);
        norm = leg.getNorm();
        VPoint rightLeg4 = new VPoint(this, 2, temp.combine(1, lwidth * leg.getEntry(1) / norm, i));
        VPoint rightLeg1 = new VPoint(this, 2, temp.combine(1, lwidth * leg.getEntry(0) / norm, j));
        VPoint rightLeg3 = new VPoint(this, 2, rightLeg4.getPos().add(leg));
        VPoint rightLeg2 = new VPoint(this, 2, rightLeg1.getPos().add(leg));
        //left leg
        VPoint leftLeg1 = new VPoint(this, 2, reflect(rightLeg4.getPos(), pos, j));
        VPoint leftLeg4 = new VPoint(this, 2, reflect(rightLeg1.getPos(), pos, j));
        VPoint leftLeg2 = new VPoint(this, 2, reflect(rightLeg3.getPos(), pos, j));
        VPoint leftLeg3 = new VPoint(this, 2, reflect(rightLeg2.getPos(), pos, j));
        //tail
        VPoint tail2 = new VPoint(this, 2, pos.combine(1, -length / 2 - tlength, j));
        VPoint tail1 = new VPoint(this, 2, pos.combine(1, -length / 2, j).combine(1, twidth / 2, i));
        VPoint tail3 = new VPoint(this, 2, reflect(tail1.getPos(), pos, j));
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
        for (int i = 1; i < spinnerets.size(); i++) {// does not work for some reason
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
}
