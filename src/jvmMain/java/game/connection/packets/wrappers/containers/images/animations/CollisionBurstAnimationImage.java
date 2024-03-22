package game.connection.packets.wrappers.containers.images.animations;

import game.connection.packets.messages.ServerMessage;
import game.connection.packets.wrappers.containers.images.ArrayRealVectorImage;
import game.output.animations.CollisionBurstAnimation;
import game.util.DevConfig;
import game.world.Point;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.Serial;

import static game.util.DevConfig.doublePrecision;
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

    public CollisionBurstAnimationImage(@NotNull ServerMessage.AnimationM.CollisionBurstM message) {
        intensity = (float) (message.getIntensity()/doublePrecision);
        pos = ArrayRealVectorImage.getVector(message.getPos());
        color = getColor(message.getColor());
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

    @Override
    public ServerMessage.AnimationM getMessage() {
        return ServerMessage.AnimationM.newBuilder()
                .setCollisionBurstM(ServerMessage.AnimationM.CollisionBurstM.newBuilder()
                        .setColor(getMessage(color))
                        .setIntensity((int) (intensity * doublePrecision))
                        .setPos(ArrayRealVectorImage.getMessage(pos))
                        .build())
                .build();
    }

    @NotNull
    private static ServerMessage.AnimationM.CollisionBurstM.Color getMessage(@NotNull Color color) {
        return ServerMessage.AnimationM.CollisionBurstM.Color.newBuilder()
                .setR(color.getRed())
                .setG(color.getGreen())
                .setB(color.getBlue())
                .build();
    }

    private static Color getColor(ServerMessage.AnimationM.CollisionBurstM.Color message) {
        return new Color(message.getR(), message.getG(), message.getB());
    }
}
