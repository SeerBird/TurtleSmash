package game.output.ui.rectangles;

import game.input.InputControl;
import game.output.ui.Focusable;
import game.output.ui.TurtleMenu;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;


public class Textbox extends Label implements Focusable {
    final String defaultText;
    Consumer<String> action;
    String currentDefaultText;

    public Textbox(double x, double y, int width, int height, String defaultText, Consumer<String> action) {
        super(x, y, width, height, defaultText);
        this.defaultText = defaultText;
        currentDefaultText = defaultText;
        this.action=action;
    }

    @Override
    public void release() {
        enter();
    }

    @Override
    public void enter() {
        TurtleMenu.focus(this);
    }

    @Override
    public void leave() {
        text = currentDefaultText;
    }

    public void useValue() {
        currentDefaultText = text;
        if (Objects.equals(text, "")) {
            currentDefaultText = defaultText;
        }
        action.accept(String.valueOf(currentDefaultText));
    }
}
