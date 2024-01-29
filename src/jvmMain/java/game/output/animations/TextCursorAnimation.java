package game.output.animations;

import game.output.ui.TurtleMenu;
import game.output.ui.rectangles.Textbox;
import game.util.DevConfig;
import javafx.util.Pair;

import java.awt.*;

public class TextCursorAnimation implements Animation {
    public Textbox textbox;
    public int frameCounter;
    public boolean lineOn;

    public TextCursorAnimation(Textbox textbox) {
        this.textbox = textbox;
        lineOn = false;
        resetFrameCounter();
    }

    private void resetFrameCounter() {
        frameCounter = 90;
        lineOn ^= true;
    }

    @Override
    public boolean drawNext(Graphics g) {
        if (TurtleMenu.getFocused() != textbox) {
            return false;//shouldn't trigger as the textbox removes this animation first.
        }
        if (textbox.textChanged) {
            resetFrameCounter();
            lineOn = true;
        }
        frameCounter--;
        if (frameCounter <= 0) {
            resetFrameCounter();
        }
        if (lineOn) {
            g.setColor(textbox.textColor);
            int x = textbox.x + textbox.width / 2 + g.getFontMetrics().stringWidth(textbox.text) / 2 + 2;
            int y = textbox.y + textbox.height / 2 - g.getFontMetrics().getHeight() + 3;
            g.drawLine(x, y, x, y + g.getFontMetrics().getHeight());
        }
        return true;
    }
}
