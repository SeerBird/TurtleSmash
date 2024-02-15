package game.connection.packets.containers.images.animations;

import game.output.animations.CollisionBurstAnimation;
import game.util.DevConfig;
import game.world.Point;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.Serial;

import static game.util.Maths.randomUnitVector;

public class CollisionBurstAnimationImage extends AnimationImage<CollisionBurstAnimation> {
    @Serial
    private static final long serialVersionUID = 800854;
    public ArrayRealVector pos;
    public float intensity;
    public Color color;

    public CollisionBurstAnimationImage(CollisionBurstAnimation animation) {
        super(animation);
    }

    public void makeImage(@NotNull CollisionBurstAnimation animation) {
        intensity = (float) animation.intensity;
        pos = animation.pos;
        color = animation.color;
    }

    @Override
    public CollisionBurstAnimation restoreAnimation() {
        CollisionBurstAnimation animation = new CollisionBurstAnimation();
        animation.color = color;
        for (int i = 0; i < intensity; i++) {
            animation.particles.put(new game.world.Point(1, pos), DevConfig.particleLingerFrames);
        }
        for (Point p : animation.particles.keySet()) {
            p.accelerate(randomUnitVector().mapMultiply(Math.random() * intensity * 5));
        }
        return animation;
    }
}
