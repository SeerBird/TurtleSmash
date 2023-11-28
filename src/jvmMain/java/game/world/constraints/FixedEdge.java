package game.world.constraints;

import game.util.DevConfig;
import game.world.BPoint;
import org.apache.commons.math3.linear.ArrayRealVector;

public class FixedEdge extends Edge {
    Double rest_d;


    public FixedEdge(BPoint p1, BPoint p2, double rest_d) {
        super(p1, p2);
        this.rest_d = rest_d;
    }


    public FixedEdge(BPoint p1, BPoint p2) {
        super(p1, p2);
        this.rest_d = p1.getDistance(p2).getNorm();
    }

    public boolean satisfy() {
        ArrayRealVector distance = p1.getDistance(p2);
        double norm = distance.getNorm();
        double displacement = (norm - rest_d);
        if (Math.abs(displacement) > DevConfig.constraintTolerance) {
            double inertia = p2.getMass() / (p1.getMass() + p2.getMass());
            p1.move(distance.mapMultiply(displacement / norm * inertia));
            p2.move(distance.mapMultiply(displacement / norm * (inertia - 1)));
            return false;
        } else {
            return true;
        }
    }

    public double getExtension() {
        return p1.getDistance(p2).getNorm() / rest_d;
    }

    public double getRestDistance() {
        return rest_d;
    }
}
