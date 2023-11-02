package game.output;


import game.Config;
import game.input.InputControl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferStrategy;

public class GameWindow extends JFrame {
    BufferStrategy strategy;

    public GameWindow() {
        setIgnoreRepaint(true);
        setResizable(false);
        setSize(Config.WIDTH, Config.HEIGHT);
        this.setLocation(400, 10);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Renderer.resize(e.getComponent().getWidth(), e.getComponent().getHeight());
            }
        });

        //region Add canvas(for buffer strategy I think?? might be unnecessary)
        Canvas canvas = new Canvas();
        canvas.setIgnoreRepaint(true);
        canvas.setSize(Config.WIDTH, Config.HEIGHT);
        add(canvas);
        pack();
        //endregion
        //region buffer strategy
        canvas.createBufferStrategy(2);
        strategy = canvas.getBufferStrategy();
        //endregion
        //region hookup game.input
        InputControl input = new InputControl();
        addKeyListener(input);
        canvas.addKeyListener(input);
        addMouseListener(input);
        canvas.addMouseListener(input);
        addMouseMotionListener(input);
        canvas.addMouseMotionListener(input);
        //endregion
        setVisible(true);
    }
    public void showCanvas() {
        strategy.show();
    }

    public Graphics getCanvas() {
        return strategy.getDrawGraphics();
    }
}