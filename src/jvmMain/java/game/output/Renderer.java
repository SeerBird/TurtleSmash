package game.output;


import game.Config;
import game.EventManager;
import game.output.ui.IElement;
import game.output.ui.TurtleMenu;
import game.world.VPoint;
import game.world.World;
import game.world.bodies.Body;
import game.world.bodies.Web;
import game.world.constraints.DistanceConstraint;
import org.jetbrains.annotations.NotNull;

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

        // bodies
        for (Body b : world.getBodies()) {
            drawBody(g, b);
        }
        g.dispose();
    }

    private void drawWeb(@NotNull Graphics g, @NotNull Web w) {


    }

    private void drawBody(@NotNull Graphics g, @NotNull Body b) {
        g.setColor(b.edgeColor);
        for (DistanceConstraint e : b.getEdges()) {
            g.drawLine((int) e.getEdge1().getX(), (int) e.getEdge1().getY(), (int) e.getEdge2().getX(), (int) e.getEdge2().getY());
        }
        g.setColor(b.pointColor);
        for (VPoint p : b.getPoints()) {
            g.fillRect((int) p.getX()-3, (int) p.getY()-3, 6, 6);
        }
    }

    public void drawImage(@NotNull Graphics g, @NotNull TurtleMenu menu) { // get all the visible objects, effects, and particles on an image
        g.setColor(Color.black);
        g.fillRect(0, 0, width, height);
        for (IElement e : menu.getElements()) {
            g.drawImage(e.getImage(), (int) Math.round(e.getPos().getEntry(0)), (int) Math.round(e.getPos().getEntry(0)), null);
        }
        g.dispose();
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }
}

