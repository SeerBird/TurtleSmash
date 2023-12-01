package game.output.ui.rectangles;


import java.awt.*;

public class GButton extends Label {
    private Runnable action;
    private boolean pressed;

    public GButton(int x, int y, int width, int height, Runnable action, String text, Color textColor) {
        super(x, y, width, height, text, textColor);
        this.action = action;
        pressed = false;
    }

    @Override
    public boolean press(double x, double y) {
        pressed = super.press(x, y);
        return pressed;
    }

    @Override
    public void release() {
        if (pressed) {//unnecessary?
            action.run();
        }
        pressed = false;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    public boolean isPressed() {
        return pressed;
    }
}
