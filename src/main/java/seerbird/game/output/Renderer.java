package seerbird.game.output;


import org.jetbrains.annotations.NotNull;
import seerbird.game.Config;
import seerbird.game.EventManager;
import seerbird.game.output.ui.IElement;
import seerbird.game.output.ui.Menu;
import seerbird.game.world.Web;
import seerbird.game.world.World;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Renderer {
    BufferedImage view;
    int height;
    int width;
    EventManager handler;

    public Renderer(EventManager handler) {
        this.handler = handler;
        width = Config.WIDTH;
        height = Config.HEIGHT;
        view = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public void update() { //get new info and progress animations

    }

    private int xToScreen(double x) { // x origin at center of the screen
        return (int) (Config.WIDTH / 2 + x * Config.TILE_SIZE);
    }

    private int yToScreen(double y) { // x origin at center of the screen
        return (int) (Config.HEIGHT / 2 - y * Config.TILE_SIZE);
    }

    public void drawImage(@NotNull Graphics g, @NotNull World world) { // get all the visible objects, effects, and particles on an image
        //background
        g.setColor(Config.BACKGROUND);
        g.fillRect(0, 0, width, height);

        // webs
        g.setColor(Color.WHITE);
        for (Web w : world.getWebs()) {
            drawWeb(g, w);
        }

        //turtles and shells
        BufferedImage bodyImage;
        g.dispose();
    }

    private void drawWeb(@NotNull Graphics g, @NotNull Web w) {


    }

    public void drawImage(@NotNull Graphics g, @NotNull Menu menu) { // get all the visible objects, effects, and particles on an image
        g.setColor(Color.black);
        g.fillRect(0, 0, width, height);
        for (IElement e : menu.getElements()) {
            g.drawImage(e.getImage(), Math.round(e.getPos().x), Math.round(e.getPos().y), null);
        }
        g.dispose();
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }
}

