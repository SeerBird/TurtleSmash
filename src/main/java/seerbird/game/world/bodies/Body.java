package seerbird.game.world.bodies;

import seerbird.game.CONSTANTS;
import seerbird.game.world.VPoint;
import seerbird.game.world.constraints.Constraint;

import java.util.ArrayList;

public class Body {
    ArrayList<VPoint> points;
    ArrayList<Constraint> constraints;

    public Body() {
        points = new ArrayList<>();
        constraints = new ArrayList<>();
    }

    public void move() {
        //forces and stuff

        //move
        for (VPoint p : points) {
            p.move();
        }
        //satisfy constraints
        boolean satisfied = false;
        int count = 0;
        while (!satisfied && count <= CONSTANTS.constrainAttempts) {
            for (Constraint c : constraints) {
                satisfied |= c.satisfy();
            }
            count++;
        }
    }
}
