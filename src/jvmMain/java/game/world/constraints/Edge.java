package game.world.constraints;

import game.util.CONSTANTS;
import game.world.BPoint;
import javafx.util.Pair;
import org.apache.commons.math3.linear.ArrayRealVector;

public class Edge implements Constraint {
    BPoint p1;
    BPoint p2;
    double rest_d;

    public Edge(BPoint p1, BPoint p2, double distance) {
        this.p1 = p1;
        this.p2 = p2;
        this.rest_d = distance;
    }
    public Edge(BPoint p1, BPoint p2){
        this(p1,p2,p1.getDistance(p2).getNorm());
    }

    public boolean satisfy() {
        ArrayRealVector distance = p1.getDistance(p2);
        double norm = distance.getNorm();
        double displacement = (norm - rest_d);
        if (Math.abs(displacement) > CONSTANTS.constraintTolerance) {
            double inertia = p2.getMass() / (p1.getMass() + p2.getMass());
            p1.move(distance.mapMultiply(displacement / norm * inertia));
            p2.move(distance.mapMultiply(displacement / norm * (inertia - 1)));
            return false;
        } else {
            return true;
        }
    }

    public Pair<BPoint, BPoint> getPoints() {
        return new Pair<>(p1, p2);
    }
    public BPoint getEdge1(){
        return p1;
    }
    public BPoint getEdge2(){
        return p2;
    }

    public void move(ArrayRealVector v) {
        getPoints().getKey().move(v);
        getPoints().getValue().move(v);
    }

    public double getDistance() {
        return rest_d;
    }
}
