package game.world.constraints;

import game.util.DevConfig;
import game.world.BPoint;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.math3.linear.ArrayRealVector;

public class ControlEdge extends Edge {
    MutableDouble rest_d;

    public ControlEdge(BPoint p1, BPoint p2, MutableDouble rest_d) {
        super(p1, p2);
        this.rest_d = rest_d;
    }

    @Override
    public boolean satisfy() {
        ArrayRealVector distance = p1.getDistance(p2);
        double norm = distance.getNorm();
        double displacement = (norm - rest_d.doubleValue());
        if (Math.abs(displacement) > DevConfig.constraintTolerance) {
            double inertia = p2.getMass() / (p1.getMass() + p2.getMass());
            p1.move(distance.mapMultiply(displacement / norm * inertia));
            p2.move(distance.mapMultiply(displacement / norm * (inertia - 1)));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public double getRestDistance() {
        return rest_d.doubleValue();
    }

    @Override
    public double getExtension() {
        return getDistance() / rest_d.doubleValue();
    }
}
