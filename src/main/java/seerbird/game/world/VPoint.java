package seerbird.game.world;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.jetbrains.annotations.NotNull;
import seerbird.game.world.bodies.Body;


public class VPoint implements Cloneable {
    ArrayRealVector pos;
    ArrayRealVector lpos;
    double mass;
    Body parent;

    public VPoint(Body body, double mass, double x, double y) {
        pos = new ArrayRealVector(2);
        pos.setEntry(0, x);
        pos.setEntry(1, y);
        lpos = pos.copy();
        this.mass = mass;
        this.parent = body;
    }

    public VPoint(Body body, double mass, @NotNull ArrayRealVector pos) {
        this.pos = pos.copy();
        lpos = pos.copy();
        this.mass = mass;
        this.parent = body;
    }

    public void move() {
        ArrayRealVector temp = pos.copy();
        pos.combineToSelf(2, -1, lpos);
        lpos = temp;
    }

    public void move(RealVector v) {
        pos.combineToSelf(1, 1, v);
    }

    public double getMass() {
        return this.mass;
    }

    public Body getParent() {
        return parent;
    }

    public void setParent(Body p) {
        this.parent = p;
    }

    public double getX() {
        return this.pos.getEntry(0);
    }

    public double getY() {
        return this.pos.getEntry(1);
    }

    public void setX(double x) {
        this.pos.setEntry(0, x);
    }

    public void setY(double y) {
        this.pos.setEntry(1, y);
    }

    public void addToX(double dx) {
        this.pos.addToEntry(0, dx);
    }

    public void addToY(double dy) {
        this.pos.addToEntry(1, dy);
    }

    public void shift(ArrayRealVector v) {
        pos.combineToSelf(1, 1, v);
        lpos.combineToSelf(1, 1, v);
    }

    public void accelerate(double x, double y) {
        pos.addToEntry(0, x);
        pos.addToEntry(1, y);
    }

    public void accelerate(RealVector v) {
        pos.combineToSelf(1, 1, v);
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

    public ArrayRealVector getDistance(@NotNull VPoint b) {
        return parent.getWorld().getDistance(pos, b.getPos());
    }

    public ArrayRealVector getDistance(ArrayRealVector pos) {
        return this.pos.copy().combineToSelf(-1, 1, pos);
    }

    public Body getBody() {
        return this.parent;
    }

    public ArrayRealVector getVelocity() {
        return parent.getWorld().getDistance(pos, lpos);
    }

    @Override
    public VPoint clone() {
        try {
            VPoint clone = (VPoint) super.clone();
            clone.lpos = this.lpos.copy();
            clone.pos = this.pos.copy();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public void stop() {
        lpos = pos.copy();
    }
}
