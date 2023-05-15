package game.output.ui;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

public abstract class IElement {
    TurtleMenu menu;
    public int x;
    public int y;

    public IElement(double x, double y) {
        this.x = (int) x;
        this.y = (int) y;
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

    private Area getArea(BufferedImage image, int maxAlpha) {
        if (image == null) return null;
        Area area = new Area();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int pAlpha = new Color(image.getRGB(x, y)).getAlpha();
                if ((pAlpha <= maxAlpha)) {
                    Rectangle r = new Rectangle(x, y, 1, 1);
                    area.add(new Area(r));
                }
            }
        }
        return area;
    }
}
