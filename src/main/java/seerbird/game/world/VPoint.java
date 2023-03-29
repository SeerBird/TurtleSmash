package seerbird.game.world;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.jetbrains.annotations.NotNull;


public class VPoint {
    ArrayRealVector pos;
    ArrayRealVector lpos;
    double mass;
    World world;

    public VPoint(World world, double mass, double x, double y) {
        pos = new ArrayRealVector(2);
        pos.setEntry(0, x);
        pos.setEntry(1, y);
        lpos = pos.copy();
        this.mass = mass;
        this.world = world;
    }

    public VPoint(World world, double mass, @NotNull ArrayRealVector pos) {
        this.pos = pos.copy();
        lpos = pos.copy();
        this.mass = mass;
        this.world = world;
    }

    public void move() {
        pos.combineToSelf(2, -1, lpos);
        lpos.combineToSelf(0, 1, pos);
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

    public void teleport(ArrayRealVector displacement) {
        pos.combineToSelf(1, 1, displacement);
        lpos.combineToSelf(1, 1, displacement);
    }

    public void move(double x, double y) {
        pos.addToEntry(0, x);
        pos.addToEntry(1, y);
    }

    public void move(RealVector a) {
        pos.combineToSelf(1, 1, a);
    }

    public void accelerateMass(@NotNull ArrayRealVector a) {
        pos.combineToSelf(1, 1 / this.mass, a);
    }

    public void accelerateMass(double x, double y) {
        pos.addToEntry(0, x / this.mass);
        pos.addToEntry(1, y / this.mass);
    }

    public ArrayRealVector getPos() {
        return this.pos.copy();
    }

    public ArrayRealVector getDistance(@NotNull VPoint b) {
        return this.pos.copy().combineToSelf(-1, 1, b.getPos());
    }

    public ArrayRealVector getDistance(ArrayRealVector pos) {
        return this.pos.copy().combineToSelf(-1, 1, pos);
    }

    public World getWorld() {
        return this.world;
    }

    public ArrayRealVector getVelocity() {
        return world.getBorderDistance(pos, lpos);
    }
}
