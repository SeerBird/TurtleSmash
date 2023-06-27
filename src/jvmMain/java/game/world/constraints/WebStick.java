package game.world.constraints;

import game.Config;
import game.util.CONSTANTS;
import game.world.VPoint;
import game.world.bodies.Web;
import org.apache.commons.math3.linear.ArrayRealVector;

public class WebStick extends Edge {
    public WebStick(VPoint webPoint, VPoint bodyPoint) {
        super(webPoint, bodyPoint);
    }

    @Override
    public boolean satisfy() {
        ArrayRealVector distance = p1.getDistance(p2);
        double norm = distance.getNorm();
        double displacement = (norm - rest_d);
        if (Math.abs(displacement) > CONSTANTS.constraintTolerance) {
            if (Math.abs(displacement) > Config.webMaxDisplacement) {
                ((Web)p1.getBody()).unstick(this);
            } else {
                double distribution = p2.getMass() / (p1.getMass() + p2.getMass());
                p1.move(distance.mapMultiply(displacement / norm * distribution));
                p2.move(distance.mapMultiply(displacement / norm * (distribution - 1)));
                return false;
            }
        }
        return true;
    }
}
