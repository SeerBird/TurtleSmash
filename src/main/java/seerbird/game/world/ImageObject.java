package seerbird.game.world;

import org.apache.commons.math3.linear.ArrayRealVector;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface ImageObject {
    BufferedImage getImage();

    default ArrayRealVector getPos() {
        return new ArrayRealVector(0, 0);
    }
}
