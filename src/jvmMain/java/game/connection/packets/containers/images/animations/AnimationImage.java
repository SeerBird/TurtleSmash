package game.connection.packets.containers.images.animations;

import game.output.animations.Animation;

import java.io.Serializable;

public abstract class AnimationImage<T extends Animation> implements Serializable {
    public AnimationImage(T animation) {
        makeImage(animation);
    }

    public abstract void makeImage(T animation);

    public abstract T restoreAnimation();
}
