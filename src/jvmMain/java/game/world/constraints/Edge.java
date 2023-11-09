package game.world.constraints;

import game.world.BPoint;

public abstract class Edge implements Constraint {
    BPoint p1;
    BPoint p2;

    public Edge(BPoint p1, BPoint p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public abstract boolean satisfy();

    public BPoint getEdge1() {
        return p1;
    }

    public BPoint getEdge2() {
        return p2;
    }

    public double getDistance(){
        return p1.getDistance(p2).getNorm();
    }
    public abstract double getRestDistance();
    public abstract double getExtension();
}
