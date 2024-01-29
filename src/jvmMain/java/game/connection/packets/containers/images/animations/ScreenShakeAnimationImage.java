package game.connection.packets.containers.images.animations;

import game.output.animations.ScreenShakeAnimation;
import org.jetbrains.annotations.NotNull;

public class ScreenShakeAnimationImage extends AnimationImage<ScreenShakeAnimation> {
    public double intensity;

    public ScreenShakeAnimationImage(ScreenShakeAnimation animation) {
        super(animation);
    }

    public void makeImage(@NotNull ScreenShakeAnimation animation) {
        this.intensity = animation.intensity;
    }

    @Override
    public ScreenShakeAnimation restoreAnimation() {
        return new ScreenShakeAnimation(intensity);
    }
}
