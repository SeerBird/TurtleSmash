package game.output.ui;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

public abstract class IElement {
    public int x;
    public int y;

    public IElement(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract boolean press(double x, double y);

    public boolean press(@NotNull ArrayRealVector v) {
        return press(v.getEntry(0), v.getEntry(1));
    }

    public abstract void release();

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
