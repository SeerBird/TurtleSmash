package seerbird.game.math;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

public final class Compute {
    public static boolean areClockwise(@NotNull ArrayRealVector p1, @NotNull ArrayRealVector p2, @NotNull ArrayRealVector p3) {
        ArrayRealVector v1 = p2.copy().combineToSelf(1, -1, p1.copy());
        ArrayRealVector v2 = p3.copy().combineToSelf(1, -1, p1.copy());
        return v1.getEntry(0) * v2.getEntry(1) - v2.getEntry(0) * v1.getEntry(1) < 0;
    }
}
