package game.connection.packets.containers.images.animations;

import game.output.animations.Animation;
import game.output.animations.ShellSnapFlashAnimation;

public class ShellSnapFlashAnimationImage extends AnimationImage<ShellSnapFlashAnimation> {

    public ShellSnapFlashAnimationImage() {
        super(null);
    }

    @Override
    public void makeImage(ShellSnapFlashAnimation animation) {

    }

    @Override
    public ShellSnapFlashAnimation restoreAnimation() {
        return new ShellSnapFlashAnimation();
    }
}
