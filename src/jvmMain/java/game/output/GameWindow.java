package game.output;


import game.input.InputControl;
import game.util.DevConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;

public class GameWindow extends JFrame {
    BufferStrategy strategy;

    public GameWindow() {
        setIgnoreRepaint(true);
        System.out.println(Toolkit.getDefaultToolkit().getScreenSize());
        setResizable(false);
        setSize(DevConfig.WIDTH, DevConfig.HEIGHT);
        this.setLocation(-10,0);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //region Add canvas(for buffer strategy I think?? might be unnecessary)
        Canvas canvas = new Canvas();
        canvas.setIgnoreRepaint(true);
        canvas.setSize(DevConfig.WIDTH, DevConfig.HEIGHT);
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