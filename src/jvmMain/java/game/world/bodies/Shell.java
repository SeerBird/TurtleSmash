package game.world.bodies;

import game.Config;
import game.world.BPoint;
import game.world.CollisionData;
import game.world.constraints.Edge;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static game.util.Maths.*;

public class Shell extends Body {
    int size;
    Turtle parent;
    ArrayList<Edge> straps;

    public Shell(@NotNull ArrayRealVector pos, @NotNull Turtle parent) {
        super();
        straps = new ArrayList<>();
        this.parent = parent;
        double size = Config.turtleSize/6;
        pos.combineToSelf(1,1,new ArrayRealVector(new Double[]{0.0,-25.0*size}));
        BPoint p1 = new BPoint(this, 1, pos.getEntry(0) + 140*size, pos.getEntry(1) + 275*size);
        BPoint p2 = new BPoint(this, 1, pos.getEntry(0) + 200*size, pos.getEntry(1) + 150*size);
        BPoint p3 = new BPoint(this, 1, reflect(p2.getPos(), pos, i));
        BPoint p4 = new BPoint(this, 1, reflect(p1.getPos(), pos, i));
        BPoint p5 = new BPoint(this, 1, reflect(p4.getPos(), pos, j));
        BPoint p6 = new BPoint(this, 1, reflect(p3.getPos(), pos, j));
        BPoint p7 = new BPoint(this, 1, reflect(p2.getPos(), pos, j));
        BPoint p8 = new BPoint(this, 1, reflect(p1.getPos(), pos, j));
        addPoints(p2, p3, p6, p7, p1, p4, p5, p8);
        addEdgeChain(p1, p2, p3, p4, p5, p6, p7, p8, p1);
        //structure
        addEdge(p2, p7);
        addEdge(p3, p6);
        addEdge(p1, p4);
        addEdge(p8, p5);
        addEdge(p2, p6);
        addEdge(p7, p4);
        //straps
        int i = 0;
        for (BPoint p : this.parent.getShellAttachment()) {
            straps.add(new Edge(p, points.get(i)));
            i++;
        }
    }

    @Override
    public void collide(@NotNull CollisionData collision) {
        Body b2 = collision.getEdge1().getParentBody();
        if (b2.getClass() == Turtle.class) {
            if (b2 == parent) {
                return;
            }
        }
        super.collide(collision);
    }

    @Override
    public boolean constrain() {
        boolean sat = super.constrain();
        for (Edge e : straps) {
            sat &= e.satisfy();
        }
        return sat;
    }

    @Override
    public void gravitate(Body b) {
        if (b != parent) {
            super.gravitate(b);
        }
    }

    @Override
    public ArrayList<Edge> getSides() {
        ArrayList<Edge> sides=new ArrayList<>();
        for(int i=0;i<8;i++){
            sides.add(edges.get(i));
        }
        return sides;
    }
}
