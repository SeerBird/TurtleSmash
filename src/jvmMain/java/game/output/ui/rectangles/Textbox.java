package game.output.ui.rectangles;

import game.output.Renderer;
import game.output.animations.Animation;
import game.output.animations.TextCursorAnimation;
import game.output.ui.Focusable;
import game.output.ui.TurtleMenu;

import java.awt.*;
import java.util.Objects;
import java.util.function.Consumer;


public class Textbox extends Label implements Focusable {
    final String defaultText;
    Consumer<String> action;
    String currentDefaultText;
    Animation cursorAnimation;

    public Textbox(int x, int y, int width, int height, String defaultText, Consumer<String> action, Color textColor) {
        super(x, y, width, height, defaultText, textColor);
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
        cursorAnimation = Renderer.addAnimation(new TextCursorAnimation(this));
    }

    @Override
    public void leave() {
        text = currentDefaultText;
        Renderer.removeAnimation(cursorAnimation);
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
