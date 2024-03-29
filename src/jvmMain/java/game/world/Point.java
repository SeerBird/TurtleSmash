package game.world;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.jetbrains.annotations.NotNull;

public class Point {
    ArrayRealVector pos;
    ArrayRealVector lpos;
    double mass;
    public Point(double mass, @NotNull ArrayRealVector pos){
        this.pos = pos.copy();
        lpos = pos.copy();
        this.mass = mass;
    }
    public Point(double mass, double x, double y){
        pos = new ArrayRealVector(new Double[]{x,y});
        lpos = pos.copy();
        this.mass = mass;
    }

    public void move() {
        ArrayRealVector temp = pos.copy();
        pos.combineToSelf(2, -1, lpos);
        lpos = temp;
    }

    public void move(RealVector v) {
        pos.combineToSelf(1, 1, v);
    }

    public void accelerate(RealVector v) {
        lpos.combineToSelf(1, -1, v);
    }

    public void shift(ArrayRealVector v) {
        pos.combineToSelf(1, 1, v);
        lpos.combineToSelf(1, 1, v);
    }

    public void setPos(@NotNull ArrayRealVector v) {
        pos = v.copy();
    }

    public double getMass() {
        return this.mass;
    }

    public double getX() {
        return this.pos.getEntry(0);
    }

    public double getY() {
        return this.pos.getEntry(1);
    }

    public void accelerate(double x, double y) {
        pos.addToEntry(0, x);
        pos.addToEntry(1, y);
    }

    public void accelerateMass(@NotNull RealVector v) {
        lpos.combineToSelf(1, -1 / this.mass, v);
    }

    public void accelerateMass(double x, double y) {
        lpos.addToEntry(0, -x / this.mass);
        lpos.addToEntry(1, -y / this.mass);
    }

    public ArrayRealVector getPos() {
        return this.pos.copy();
    }

    public ArrayRealVector getDistance(@NotNull BPoint b) {
        return World.getDistance(pos, b.getPos()); // could be game.world-independent? just geometry if I don't have borderDistance
    }

    public ArrayRealVector getDistance(ArrayRealVector pos) {
        return World.getDistance(this.pos, pos); // could be game.world-independent? just geometry if I don't have borderDistance
    }

    public ArrayRealVector getVelocity() {
        return World.getDistance(lpos,pos);
    }

    public double project(@NotNull ArrayRealVector normalizedAxis) {
        return getPos().dotProduct(normalizedAxis);
    }

    public void stop() {
        lpos = pos.copy();
    }
}
