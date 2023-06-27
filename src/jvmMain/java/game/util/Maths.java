package game.util;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

public final class Maths {
    public static boolean areClockwise(@NotNull ArrayRealVector p1, @NotNull ArrayRealVector p2, @NotNull ArrayRealVector p3) {
        return (p3.getEntry(1) - p1.getEntry(1)) * (p2.getEntry(0) - p1.getEntry(0)) <
                (p2.getEntry(1) - p1.getEntry(1)) * (p3.getEntry(0) - p1.getEntry(0));
    }

    public static boolean intersect(@NotNull ArrayRealVector p1, @NotNull ArrayRealVector p2,
                                    @NotNull ArrayRealVector p3, @NotNull ArrayRealVector p4) {
        return (areClockwise(p1, p3, p4) ^ areClockwise(p2, p3, p4)) &&
                (areClockwise(p1, p2, p3) ^ areClockwise(p1, p2, p4));
    }

    public static ArrayRealVector i = new ArrayRealVector(new Double[]{1.0, 0.0});
    public static ArrayRealVector j = new ArrayRealVector(new Double[]{0.0, 1.0});
}
