package seerbird.game.world;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.Area;

public abstract class TurtleBody implements ImageObject {
    Shape shape;
    ArrayRealVector pos;
    ArrayRealVector lpos;
    double rotation;
    double lrotation;
    double mass;
    double collisionElasticity = 1;
    World world;

    public TurtleBody(World world, double mass, double x, double y) {
        pos = new ArrayRealVector(2);
        pos.setEntry(0, x);
        pos.setEntry(1, y);
        lpos = pos.copy();
        rotation = 0;
        lrotation = 0;
        this.mass = mass;
        shape = new Area();
        this.world = world;
    }

    public void update() {
        move();
        rotate();
    }

    public double getMass() {
        return this.mass;
    }

    public Shape getShape() {
        return this.shape;
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

    public void move() {
        pos.combineToSelf(2, -1, lpos);
        lpos.combineToSelf(0, 1, pos);
    }

    public void teleport(ArrayRealVector displacement) {
        pos.combineToSelf(1, 1, displacement);
        lpos.combineToSelf(1, 1, displacement);
    }

    public void accelerate(double x, double y) {
        pos.addToEntry(0, x);
        pos.addToEntry(1, y);
    }

    public void accelerate(ArrayRealVector a) {
        pos.combineToSelf(1, 1, a);
    }

    public void accelerateMass(@NotNull ArrayRealVector a) {
        pos.combineToSelf(1, 1 / this.mass, a);
    }

    public void accelerateMass(double x, double y) {
        pos.addToEntry(0, x / this.mass);
        pos.addToEntry(1, y / this.mass);
    }

    private void gravitate(@NotNull ArrayRealVector pos, double mass) {

    }

    public ArrayRealVector getPos() {
        return this.pos.copy();
    }


    public ArrayRealVector getDistance(@NotNull TurtleBody b) {
        return this.pos.copy().combineToSelf(1, -1, b.getPos());
    }

    public ArrayRealVector getDistance(ArrayRealVector pos) {
        return this.pos.copy().combineToSelf(-1, 1, pos);
    }

    public void rotate() {
        rotation = (rotation * 2 - lrotation) % (2 * Math.PI);
    }

    public void accelerate_rotation(double a) {
        rotation = (rotation + a) % (2 * Math.PI);
    }

    public World getWorld() {
        return this.world;
    }

    public ArrayRealVector getVelocity() {
        return world.getBorderDistance(pos, lpos);
    }
}
