package game.output.ui.rectangles;

import game.input.InputControl;
import game.output.Renderer;
import game.output.animations.TextCursorAnimation;
import game.output.ui.Focusable;
import game.output.ui.TurtleMenu;
import game.util.DevConfig;

import java.awt.*;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;


public class Textbox extends Label implements Focusable {
    final String defaultText;
    public Color color = DevConfig.turtle;
    Consumer<String> action;
    String currentDefaultText;
    int cursorAnimationID = -1;

    public Textbox(double x, double y, int width, int height, String defaultText, Consumer<String> action) {
        super(x, y, width, height, defaultText);
        this.defaultText = defaultText;
        currentDefaultText = defaultText;
        this.action = action;
    }

    @Override
    public void release() {
        enter();
    }

    @Override
    public void enter() {
        TurtleMenu.focus(this);
        cursorAnimationID = Renderer.addAnimation(new TextCursorAnimation(this));
    }

    @Override
    public void leave() {
        text = currentDefaultText;
        Renderer.removeAnimation(cursorAnimationID);
    }

    public void setText(String string) {
        this.text = string;
    }

    public void useValue() {
        currentDefaultText = text;
        if (Objects.equals(text, "")) {
            currentDefaultText = defaultText;
        }
        action.accept(String.valueOf(currentDefaultText));
    }
}
