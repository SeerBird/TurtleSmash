package game.output.animations;

import game.util.DevConfig;
import game.util.Util;
import game.world.CollisionData;
import game.world.Point;
import game.world.World;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static game.util.Maths.randomUnitVector;

public class CollisionBurstAnimation implements Animation {
    public Map<Point, Double> particles;
    public ArrayList<Point> removed;
    public Color color;
    public ArrayRealVector pos;
    public double intensity;

    public CollisionBurstAnimation(@NotNull CollisionData collision) {
        particles = new HashMap<>();
        removed = new ArrayList<>();
        intensity = Math.pow(collision.overlap.getNorm(), 3) * DevConfig.burstIntensity;
        pos = collision.getVertex().getPos();
        double random = Math.random();
        for (int i = 0; i < intensity && i < DevConfig.maxParticles; i++) {
            particles.put(new Point(1, pos), DevConfig.particleLingerFrames);
        }
        for (Point p : particles.keySet()) {
            p.accelerate(randomUnitVector(random).mapMultiply(((random * 10) % 1) * intensity * 5));
            random = (random * 10) % 1;
        }
        color = Util.getColor(collision.vertex.getParentBody());
    }

    public CollisionBurstAnimation() {
        particles = new HashMap<>();
        removed = new ArrayList<>();
    }

    @Override
    public boolean drawNext(@NotNull Graphics g) {
        //region update and draw particles
        g.setColor(color);
        ArrayRealVector v;
        for (Point p : removed) {
            particles.remove(p);
        }
        for (Point p : particles.keySet()) {
            //region linger
            if (particles.get(p) < DevConfig.particleLingerFrames) {
                particles.put(p, particles.get(p) - 1);
                if (particles.get(p) == 0 || World.isOutOfBounds(p.getPos())) {
                    removeParticle(p);
                }
            }
            //endregion
            //region decelerate
            else if ((v = p.getVelocity()).getNorm() > 0.01) {
                p.move();
                p.stop();
                p.accelerate(v.mapMultiply(0.9));
            } else {
                particles.put(p, particles.get(p) - 1);
            }
            //endregion
            //region draw
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(),
                    (int) (255 * particles.get(p) / DevConfig.particleLingerFrames))); //fade
            g.fillRect((int) p.getX() - 1, (int) p.getY() - 1, 2, 2);
            //endregion
        }
        //endregion
        return !(particles.isEmpty());
    }

    private void removeParticle(Point p) {
        removed.add(p);
    }
}
