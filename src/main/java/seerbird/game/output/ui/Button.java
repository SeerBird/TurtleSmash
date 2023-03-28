package seerbird.game.output.ui;

import javax.imageio.ImageIO;
import javax.vecmath.Vector2f;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class Button extends IElement {
    private Runnable action;
    private final Vector2f speed;
    private final BufferedImage unpressedImage;
    private final BufferedImage pressedImage;
    URL unpressedURL;
    URL pressedURL;
    private boolean pressed;

    public Button(float x, float y, Runnable action) {
        super(x, y);
        try {
            unpressedURL = getClass().getResource("button.png");
            pressedURL = getClass().getResource("buttonPressed.png");
        } catch (NullPointerException e) {
            throw new RuntimeException(e);
        }
        try {
            unpressedImage = ImageIO.read(unpressedURL);
            pressedImage = ImageIO.read(pressedURL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.action = action;
        pressed = false;
        speed = new Vector2f(0, 0);
        shape = getArea(unpressedImage, 255);
    }

    public void move() {
        pos.add(speed);
    }

    @Override
    public Image getImage() {
        if (pressed) {
            return pressedImage;
        }
        return unpressedImage;
    }

    @Override
    public boolean press(float x, float y) {
        pressed = super.press(x, y);
        return pressed;
    }

    @Override
    public void release() {
        pressed = false;
    }

    public boolean isPressed() {
        return pressed;
    }

    public void accelerate(int direction) {
        if (direction == 0) {
            this.speed.y -= 1;
        } else if (direction == 1) {
            this.speed.x += 1;
        } else if (direction == 2) {
            this.speed.y += 1;
        } else {
            this.speed.x -= 1;
        }
    }

    public void setPos(int x, int y) {
        this.pos.x = x;
        this.pos.y = y;
    }
}
