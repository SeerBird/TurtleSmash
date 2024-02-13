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

    public static Class<? extends AnimationImage<?>> getImageClass(@NotNull Animation animation) {
        Class<? extends Animation> clazz = animation.getClass();
        if (clazz.equals(CollisionBurstAnimation.class)) {
            return CollisionBurstAnimationImage.class;
        } else if (clazz.equals(ScreenShakeAnimation.class)) {
            return ScreenShakeAnimationImage.class;
        } else {
            return ShellSnapFlashAnimationImage.class;
        }
    }
}
