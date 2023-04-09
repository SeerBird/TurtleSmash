package seerbird.game.world.constraints;

import javafx.util.Pair;
import org.apache.commons.math3.linear.ArrayRealVector;
import seerbird.game.CONSTANTS;
import seerbird.game.world.VPoint;

public class DistanceConstraint implements Constraint {
    VPoint p1;
    VPoint p2;
    double rest_d;

    public DistanceConstraint(VPoint p1, VPoint p2, double distance) {
        this.p1 = p1;
        this.p2 = p2;
        this.rest_d = distance;
    }

    public boolean satisfy() {
        ArrayRealVector distance = p1.getDistance(p2);
        double norm = distance.getNorm();
        double displacement = norm - rest_d;
        if (Math.abs(displacement) > CONSTANTS.constraintTolerance) {
            distance.mapMultiply(displacement / norm);
            double inertia = p2.getMass() / (p1.getMass() + p2.getMass());
            p1.accelerate(distance.mapMultiply(displacement / norm * inertia));
            p2.accelerate(distance.mapMultiply(displacement / norm * (inertia - 1)));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public DistanceConstraint clone() {
        try {
            DistanceConstraint clone = (DistanceConstraint) super.clone();
            clone.p1 = p1.clone();
            clone.p2 = p2.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public Pair<VPoint, VPoint> getPoints() {
        return new Pair<>(p1, p2);
    }
    public VPoint getEdge1(){
        return p1;
    }
    public VPoint getEdge2(){
        return p2;
    }

    public void move(ArrayRealVector v) {
        getPoints().getKey().accelerate(v);
        getPoints().getValue().accelerate(v);
    }
}
