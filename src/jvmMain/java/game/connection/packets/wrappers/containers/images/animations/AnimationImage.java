package game.connection.packets.wrappers.containers.images.animations;

import game.connection.packets.messages.ServerMessage;
import game.output.animations.Animation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

public abstract class AnimationImage<T extends Animation> implements Serializable {
    public AnimationImage(T animation) {
        makeImage(animation);
    }

    protected AnimationImage() {
    }

    @Nullable
    public static AnimationImage<?> getImageFromMessage(@NotNull ServerMessage.AnimationM message){
        if(message.hasCollisionBurstM()){
            return new CollisionBurstAnimationImage(message.getCollisionBurstM());
        } else if (message.hasShellSnapFlashM()) {
            return new ShellSnapFlashAnimationImage();
        }else if (message.hasScreenShakeM()) {
            return new ScreenShakeAnimationImage(message.getScreenShakeM());
        }
        return null;
    }

    public abstract void makeImage(T animation);

    public abstract T restoreAnimation();

    public abstract ServerMessage.AnimationM getMessage();
}
