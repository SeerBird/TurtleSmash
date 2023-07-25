package game.world.bodies;

import game.Config;
import game.world.VPoint;
import game.world.World;
import game.world.constraints.Edge;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

import static game.util.Maths.i;
import static game.util.Maths.j;

public class Turtle extends Body {
    ArrayList<VPoint> spinnerets;

    public Turtle(World world, @NotNull ArrayRealVector pos, ArrayRealVector side1, ArrayRealVector side2) {
        super(world);
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
        double twidth=20;
        //head
        ArrayRealVector temp = pos.combine(1, length / 2 + hlength, j).combine(1, hwidth / 2, i);
        VPoint h1 = new VPoint(this, 2, temp);
        VPoint h2 = new VPoint(this, 2, temp.combine(1, -hlength, j));
        VPoint h3 = new VPoint(this, 2, temp.combine(1, -hwidth, i));
        VPoint h4 = new VPoint(this, 2, temp.combine(1, -hlength, j).combine(1, -hwidth, i));
        //right arm
        temp = pos.combine(1, length / 2, j).combine(1, width / 2, i);
        double norm = arm.getNorm();
        VPoint ra1 = new VPoint(this, 2, temp.combine(1, -awidth * arm.getEntry(1) / norm, i));
        VPoint ra2 = new VPoint(this, 2, temp.combine(1, -awidth * arm.getEntry(0) / norm, j));
        VPoint ra3 = new VPoint(this, 2, ra1.getPos().add(arm));
        VPoint ra4 = new VPoint(this, 2, ra2.getPos().add(arm));
        //left arm
        temp = pos.combine(1, length / 2, j).combine(1, -width / 2, i);
        norm = arm.getNorm();
        VPoint la1 = new VPoint(this, 2, temp.combine(1, awidth * arm.getEntry(1) / norm, i));
        VPoint la2 = new VPoint(this, 2, temp.combine(1, -awidth * arm.getEntry(0) / norm, j));
        arm.setEntry(0, -arm.getEntry(0));
        VPoint la3 = new VPoint(this, 2, la1.getPos().add(arm));
        VPoint la4 = new VPoint(this, 2, la2.getPos().add(arm));
        //right leg
        temp = pos.combine(1, -length / 2, j).combine(1, width / 2, i);
        norm = leg.getNorm();
        VPoint rl1 = new VPoint(this, 2, temp.combine(1, lwidth * leg.getEntry(1) / norm, i));
        VPoint rl2 = new VPoint(this, 2, temp.combine(1, lwidth * leg.getEntry(0) / norm, j));
        VPoint rl3 = new VPoint(this, 2, rl1.getPos().add(leg));
        VPoint rl4 = new VPoint(this, 2, rl2.getPos().add(leg));
        //left leg
        temp = pos.combine(1, -length / 2, j).combine(1, -width / 2, i);
        norm = leg.getNorm();
        VPoint ll1 = new VPoint(this, 2, temp.combine(1, -lwidth * leg.getEntry(1) / norm, i));
        VPoint ll2 = new VPoint(this, 2, temp.combine(1, lwidth * leg.getEntry(0) / norm, j));
        leg.setEntry(0, -leg.getEntry(0));
        VPoint ll3 = new VPoint(this, 2, ll1.getPos().add(leg));
        VPoint ll4 = new VPoint(this, 2, ll2.getPos().add(leg));
        //tail
        VPoint t1 = new VPoint(this, 2, pos.combine(1, -length / 2 - tlength, j));
        VPoint t2 = new VPoint(this,2,pos.combine(1, -length / 2, j).combine(1,twidth/2,i));
        VPoint t3 = new VPoint(this,2,pos.combine(1, -length / 2, j).combine(1,-twidth/2,i));
        //add
        addPoints(h1, h2, h3, h4, ra1, ra2, ra3, ra4, la1, la2, la3, la4, rl1, rl2, rl3, rl4, ll1, ll2, ll3, ll4, t1,t2,t3);
        addEdgeChain(h1, h2, ra1, ra3, ra4, ra2, rl2, rl4, rl3, rl1, t2,t1,t3, ll1, ll3, ll4, ll2, la2, la4, la3, la1, h4, h3, h1);
        spinnerets.addAll(new ArrayList<>(Arrays.asList(ra3,la3,rl4,ll4)));
        //structure
        addEdge(ra2,ll2);
        addEdge(la2,rl2);
        addEdge(ra1,ra4);
        addEdge(ra2,ra3);
        addEdge(la1,la4);
        addEdge(la2,la3);
        addEdge(ll1,ll4);
        addEdge(ll2,ll3);
        addEdge(rl1,rl4);
        addEdge(rl2,rl3);
        addEdge(h1,h4);
        addEdge(h2,h3);
        addEdge(ra1,la2);
        addEdge(la1,ra2);
        addEdge(rl2,t2);
        addEdge(ll2,t3);
        addEdge(h2,la2);
        addEdge(h4,ra2);
        addEdge(ra2,rl1);
        addEdge(la2,ll1);
        addEdge(rl2,ra1);
        addEdge(ll2,la1);
        addEdge(t2,t3);
        addEdge(ll2,t2);
        addEdge(rl2,t3);
    }

    @Override
    public ArrayList<Edge> getSides() {
        ArrayList<Edge> sides = new ArrayList<>();
        for(int i=0;i<23;i++){
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
        new Web(parentWorld, spinnerets.get(spinneretID), dist.add(spinnerets.get(spinneretID).getVelocity()));//FLING
    }
}
