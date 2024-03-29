package game.connection.packets.wrappers.containers.images.animations;

import game.connection.packets.messages.ServerMessage;
import game.output.animations.ShellSnapFlashAnimation;

import java.io.Serial;

public class ShellSnapFlashAnimationImage extends AnimationImage<ShellSnapFlashAnimation> {
    @Serial
    private static final long serialVersionUID = 800856;

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

    @Override
    public ServerMessage.AnimationM getMessage() {
        return ServerMessage.AnimationM.newBuilder()
                .setShellSnapFlashM(ServerMessage.AnimationM.ShellSnapFlashM.newBuilder())
                .build();
    }
}
