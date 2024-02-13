package game.output.animations;

import game.util.DevConfig;

import java.awt.*;

import static game.util.DevConfig.HIGHLIGHT;

public class ShellSnapFlashAnimation implements Animation {
    public int frames;

    public ShellSnapFlashAnimation() {
        frames = DevConfig.shellSnapFlashFrames;
    }

    @Override
    public boolean drawNext(Graphics g) {
        frames--;
        g.setColor(HIGHLIGHT);
        for (int i = 0; i < frames; i++) {
            g.drawRect(i, i, DevConfig.WIDTH - i * 2, DevConfig.HEIGHT - i * 2);
        }
        return frames > 0;
    }
}
