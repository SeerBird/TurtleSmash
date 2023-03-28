package seerbird.game.world;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.Area;

public abstract class TurtleBody implements ImageObject {
    Shape shape;
    ArrayRealVector velocity;
    ArrayRealVector pos;
    double rotation;
    double w;
    double mass;
    double collisionElasticity = 1;
    World world;

    public TurtleBody(World world, double mass, double x, double y) {
        velocity = new ArrayRealVector(2);
        pos = new ArrayRealVector(2);
        pos.setEntry(0, x);
        pos.setEntry(1, y);
        rotation = 0;
        w = 0;
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
        world.borderLink(pos.combineToSelf(1, 1, velocity));
    }

    public void accelerate(double x, double y) {
        velocity.addToEntry(0, x);
        velocity.addToEntry(1, y);
    }

    public void accelerate(ArrayRealVector a) {
        velocity.combineToSelf(1, 1, a);
    }

    public void accelerateMass(@NotNull ArrayRealVector a) {
        velocity.add(a.mapMultiply(1 / this.mass));
    }

    public void accelerateMass(double x, double y) {
        velocity.addToEntry(0, x / this.mass);
        velocity.addToEntry(1, y / this.mass);
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
        rotation = (rotation + w) % (2 * Math.PI);
    }

    public void accelerate_rotation(double dw) {
        w = (w + dw) % (2 * Math.PI);
    }

    public World getWorld() {
        return this.world;
    }

    public RealVector getVelocity() {
        return this.velocity.copy();
    }
}
