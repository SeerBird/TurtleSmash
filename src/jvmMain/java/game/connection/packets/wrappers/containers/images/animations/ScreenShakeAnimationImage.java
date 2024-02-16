package game.connection.packets.wrappers.containers.images.animations;

import game.connection.packets.messages.ServerMessage;
import game.output.animations.ScreenShakeAnimation;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;

import static game.util.DevConfig.doublePrecision;

public class ScreenShakeAnimationImage extends AnimationImage<ScreenShakeAnimation> {
    @Serial
    private static final long serialVersionUID = 800855;
    public float intensity;

    public ScreenShakeAnimationImage(ScreenShakeAnimation animation) {
        super(animation);
    }
    public ScreenShakeAnimationImage(@NotNull ServerMessage.AnimationM.ScreenShakeM message){
        intensity= (float) (message.getIntensity()/doublePrecision);
    }

    public void makeImage(@NotNull ScreenShakeAnimation animation) {
        this.intensity = (float) animation.intensity;
    }

    @Override
    public ScreenShakeAnimation restoreAnimation() {
        return new ScreenShakeAnimation(intensity);
    }

    @Override
    public ServerMessage.AnimationM getMessage() {
        return ServerMessage.AnimationM.newBuilder()
                .setScreenShakeM(ServerMessage.AnimationM.ScreenShakeM.newBuilder()
                        .setIntensity((int) (intensity*doublePrecision))).build();
    }
}
