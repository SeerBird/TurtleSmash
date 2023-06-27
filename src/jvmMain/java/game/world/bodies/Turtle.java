package game.world.bodies;

import game.Config;
import game.world.VPoint;
import game.world.World;
import game.world.constraints.Edge;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.util.ArrayList;

public class Turtle extends Body {
    ArrayList<VPoint> spinnerets;

    public Turtle(World world, ArrayRealVector pos, ArrayRealVector side1, ArrayRealVector side2) {
        super(world);
        spinnerets=new ArrayList<>();
        VPoint p1 = new VPoint(this, 10, pos);
        VPoint p2 = new VPoint(this, 10, pos.combine(1, 1, side1));
        VPoint p3 = new VPoint(this, 10, pos.combine(1, 1, side2));
        VPoint p4 = new VPoint(this, 10, pos.combine(1, 1, side1).combine(1, 1, side2));
        addEdge(new Edge(p1, p2, p1.getDistance(p2).getNorm()));
        addEdge(new Edge(p1, p3, p1.getDistance(p3).getNorm()));
        addEdge(new Edge(p2, p4, p2.getDistance(p4).getNorm()));
        addEdge(new Edge(p3, p4, p3.getDistance(p4).getNorm()));
        addEdge(new Edge(p1, p4, p1.getDistance(p4).getNorm()));
        addEdge(new Edge(p2, p3, p2.getDistance(p3).getNorm()));
        addPoint(p1);
        addPoint(p2);
        addPoint(p3);
        addPoint(p4);
        spinnerets.addAll(points);
        //refreshMass(); see what happens
    }

    @Override
    public ArrayList<Edge> getSides() {
        ArrayList<Edge> sides = new ArrayList<>();
        sides.add(edges.get(0));
        sides.add(edges.get(1));
        sides.add(edges.get(2));
        sides.add(edges.get(3));
        return sides;
    }

    public void webFling(ArrayRealVector pos) {
        ArrayRealVector dist = spinnerets.get(0).getDistance(pos);
        double minNorm = dist.getNorm();
        int spinneretID = 0;
        ArrayRealVector compareDist;
        double compareNorm;
        for (int i = 1; i < spinnerets.size(); i++) {// does not work for some reason
            compareDist = spinnerets.get(1).getDistance(pos);
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
