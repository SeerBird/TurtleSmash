package seerbird.game.world.bodies;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import seerbird.game.world.VPoint;
import seerbird.game.world.World;
import seerbird.game.world.constraints.DistanceConstraint;

import java.util.ArrayList;

public class Star extends Body {
    public Star(World world, @NotNull ArrayRealVector pos) {
        super(world);
        VPoint p1 = new VPoint(this, 1, pos.getEntry(0), pos.getEntry(1));
        VPoint p2 = new VPoint(this, 1, pos.getEntry(0) + 20, pos.getEntry(1) - 10);
        VPoint p3 = new VPoint(this, 1, pos.getEntry(0) + 40, pos.getEntry(1));
        VPoint p4 = new VPoint(this, 1, pos.getEntry(0) + 30, pos.getEntry(1) + 30);
        VPoint p5 = new VPoint(this, 1, pos.getEntry(0) + 10, pos.getEntry(1) + 30);
        points.add(p1);
        points.add(p2);
        points.add(p3);
        points.add(p4);
        points.add(p5);
        addEdge(p1,p3);
        addEdge(p1,p4);
        addEdge(p2,p4);
        addEdge(p2,p5);
        addEdge(p3,p5);
        addEdge(p1,p2);
        addEdge(p2,p3);
        addEdge(p3,p4);
        addEdge(p4,p5);
        addEdge(p5,p1);
    }
    @Override
    public ArrayList<DistanceConstraint> getSides() {
        ArrayList<DistanceConstraint> sides=new ArrayList<>();
        sides.add(edges.get(0));
        sides.add(edges.get(1));
        sides.add(edges.get(2));
        sides.add(edges.get(3));
        sides.add(edges.get(4));
        return sides;
    }
}
