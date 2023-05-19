package game.output;


import game.Config;
import game.EventManager;
import game.input.KeyboardInput;
import game.input.MouseInput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferStrategy;

public class GameWindow extends JFrame {
    private final EventManager handler;
    BufferStrategy strategy;

    public GameWindow(EventManager handler) {
        // Basic functionality
        this.handler = handler;
        setIgnoreRepaint(true);
        setResizable(false);
        setVisible(true);
        this.setLocation(400, 10);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                handler.getRenderer().resize(e.getComponent().getWidth(), e.getComponent().getHeight());
            }
        });

        // Add canvas(for buffer strategy I think?? might be unnecessary)
        Canvas canvas = new Canvas();
        canvas.setIgnoreRepaint(true);
        canvas.setSize(Config.WIDTH, Config.HEIGHT);
        add(canvas);
        pack();

        //Buffer strategy
        canvas.createBufferStrategy(2);
        strategy = canvas.getBufferStrategy();

        // Hookup game.input
        KeyboardInput keyboard = new KeyboardInput(handler);
        addKeyListener(keyboard);
        canvas.addKeyListener(keyboard);
        MouseInput mouse = new MouseInput(handler);
        addMouseListener(mouse);
        canvas.addMouseListener(mouse);
        addMouseMotionListener(mouse);
        canvas.addMouseMotionListener(mouse);//I think two of the last four lines are redundant. can't be asked to check
    }
    public void showCanvas() {
        strategy.show();
    }

    public Graphics getCanvas() {
        return strategy.getDrawGraphics();
    }
}