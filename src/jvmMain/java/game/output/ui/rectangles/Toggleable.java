package game.output.ui.rectangles;

public class Toggleable extends Label {
    protected boolean pressed;
    boolean state = false;

    public Toggleable(double x, double y, int width, int height, String text) {
        super(x, y, width, height, text);
    }

    @Override
    public boolean press(double x, double y) {
        pressed = super.press(x, y);
        return pressed;
    }

    @Override
    public void release() {
        if (pressed) {//unnecessary?
            toggle();
        }
        pressed = false;
    }

    public boolean getState() {
        return state;
    }

    public void toggle() {
        state^=true;
    }
}
