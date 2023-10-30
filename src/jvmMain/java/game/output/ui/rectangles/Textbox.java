package game.output.ui.rectangles;

import game.input.InputControl;
import game.output.ui.Focusable;
import game.output.ui.TurtleMenu;


public class Textbox extends Label implements Focusable {
    StringBuffer text;
    public Textbox(double x, double y) {
        super(x, y,4,4,"bababoi");
    }

    @Override
    public void enter() {
        TurtleMenu.focus(this);
        InputControl.connectTextInput(text);
    }

    @Override
    public void leave() {
        InputControl.disconnectTextInput();
    }
}
