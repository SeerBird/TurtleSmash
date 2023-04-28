package game.output.ui;

import org.apache.commons.math3.linear.ArrayRealVector;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

public class IElement {
    ArrayRealVector pos;
    Shape shape;

    public IElement(float x, float y) {
        pos = new ArrayRealVector(2);
    }

    public boolean press(float x, float y) {
        return shape.contains(x - pos.getEntry(0), y - pos.getEntry(1));
    }

    public void release() {
    }


    public Image getImage() {
        return null;
    }

    public Area getArea(BufferedImage image, int maxAlpha) {
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

    public ArrayRealVector getPos() {
        return pos;
    }
}
