package seerbird.game.world.bodies;

import org.apache.commons.math3.linear.ArrayRealVector;
import seerbird.game.world.VPoint;
import seerbird.game.world.World;
import seerbird.game.world.constraints.DistanceConstraint;

import java.util.ArrayList;

public class Box extends Body {
    public Box(World world, ArrayRealVector pos, ArrayRealVector side1, ArrayRealVector side2) {
        super(world);
        VPoint p1 = new VPoint(this, 10, pos);
        VPoint p2 = new VPoint(this, 10, pos.combine(1, 1, side1));
        VPoint p3 = new VPoint(this, 10, pos.combine(1, 1, side2));
        VPoint p4 = new VPoint(this, 10, pos.combine(1, 1, side1).combine(1, 1, side2));
        addEdge(new DistanceConstraint(p1, p2, p1.getDistance(p2).getNorm()));
        addEdge(new DistanceConstraint(p1, p3, p1.getDistance(p3).getNorm()));
        addEdge(new DistanceConstraint(p2, p4, p2.getDistance(p4).getNorm()));
        addEdge(new DistanceConstraint(p3, p4, p3.getDistance(p4).getNorm()));
        addEdge(new DistanceConstraint(p1, p4, p1.getDistance(p4).getNorm()));
        addEdge(new DistanceConstraint(p2, p3, p2.getDistance(p3).getNorm()));
        addPoint(p1);
        addPoint(p2);
        addPoint(p3);
        addPoint(p4);
        refreshMass();
    }

    @Override
    public ArrayList<DistanceConstraint> getSides() {
        ArrayList<DistanceConstraint> sides = new ArrayList<>();
        sides.add(edges.get(0));
        sides.add(edges.get(1));
        sides.add(edges.get(2));
        sides.add(edges.get(3));
        return sides;
    }
}
