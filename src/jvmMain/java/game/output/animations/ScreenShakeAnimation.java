package game.output.animations;

import game.output.Renderer;
import game.util.DevConfig;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.awt.*;

import static game.util.Maths.randomUnitVector;

public class ScreenShakeAnimation implements Animation {
    public double intensity;
    public ArrayRealVector translation;

    public ScreenShakeAnimation(double intensity) {
        this.intensity = intensity*DevConfig.shakeIntensity;
        translation = new ArrayRealVector(2);
    }

    @Override
    public boolean drawNext(Graphics g) {
        translation = (ArrayRealVector) randomUnitVector().mapMultiply(intensity);
        Renderer.setPos(translation);
        intensity *= DevConfig.shakeDecay;
        return !(intensity < 1);
    }
}
