package seerbird.game.output;

import seerbird.game.Config;
import seerbird.game.EventManager;
import seerbird.game.input.KeyboardInput;
import seerbird.game.input.MouseInput;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;

public class GameWindow extends JFrame {
    private final KeyboardInput keyboard;
    private final MouseInput mouse;
    private final Canvas canvas;
    private final EventManager handler;
    BufferStrategy strategy;

    public GameWindow(EventManager handler) {
        // Basic functionality
        this.handler = handler;
        setIgnoreRepaint(true);
        setVisible(true);
        this.setLocation(400,10);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                handler.postWindowResizeEvent(e);
            }
        });

        // Add canvas(for buffer strategy I think?? might be unnecessary)
        canvas = new Canvas();
        canvas.setIgnoreRepaint(true);
        canvas.setSize(Config.WIDTH, Config.HEIGHT);
        add(canvas);
        pack();

        //Buffer strategy
        canvas.createBufferStrategy(2);
        strategy = canvas.getBufferStrategy();

        // Hookup input
        keyboard = new KeyboardInput(this);
        addKeyListener(keyboard);
        canvas.addKeyListener(keyboard);
        mouse = new MouseInput(this);
        addMouseListener(mouse);
        canvas.addMouseListener(mouse);
        addMouseMotionListener(mouse);
        canvas.addMouseMotionListener(mouse);
    }

    public EventManager getHandler() {
        return this.handler;
    }

    public KeyboardInput getKeyboard() {
        return this.keyboard;
    }

    public MouseInput getMouse() {return this.mouse;}

    public void redraw(Image img) {
        /*
        // Reset image
        Graphics graphics = canvas.getGraphics();

        // Actually do things
        graphics.drawImage(img, 0, 0, null);

        // Clean up
        graphics.dispose();
        */
    }

    public void showCanvas() {
        strategy.show();
    }

    public Graphics getCanvas() {
        return strategy.getDrawGraphics();
    }
}