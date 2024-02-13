package game.connection.packets.containers.images.animations;

import game.output.animations.Animation;
import game.output.animations.CollisionBurstAnimation;
import game.output.animations.ScreenShakeAnimation;
import org.jetbrains.annotations.NotNull;

public abstract class AnimationImage<T extends Animation> {
    public AnimationImage(T animation) {
        makeImage(animation);
    }

    public abstract void makeImage(T animation);

    public abstract T restoreAnimation();
}
