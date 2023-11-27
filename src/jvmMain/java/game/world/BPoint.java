package game.world;

import game.world.bodies.Body;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;


public class BPoint extends Point {
    transient Body parentBody;

    public BPoint(Body body, double mass, double x, double y) {
        super(mass, x, y);
        this.parentBody = body;
    }

    public BPoint(Body body, @NotNull Point point) {
        super(point.mass, point.getPos());
        parentBody = body;
    }

    public BPoint(Body body, double mass, @NotNull ArrayRealVector pos) {
        super(mass, pos);
        this.parentBody = body;
    }

    @NotNull
    public Body getParentBody() {
        return parentBody;
    }

    public void setParentBody(Body p) {
        this.parentBody = p;
    }

    public BPoint copy(Body parent) {
        return new BPoint(parent, mass, pos.copy());
    }

    public void setMass(double mass) {
        this.mass=mass;
    }
}
