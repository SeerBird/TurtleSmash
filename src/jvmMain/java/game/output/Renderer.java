package game.output;


import game.Config;
import game.GameHandler;
import game.output.ui.rectangles.GButton;
import game.output.ui.IElement;
import game.output.ui.rectangles.Label;
import game.GameState;
import game.output.ui.TurtleMenu;
import game.output.ui.rectangles.PlayerList;
import game.output.ui.rectangles.ServerList;
import game.world.VPoint;
import game.world.World;
import game.world.bodies.Body;
import game.world.bodies.Web;
import game.world.constraints.Edge;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class Renderer {
    int height;
    int width;
    Graphics g;
    GameHandler handler;

    public Renderer(GameHandler handler) {
        this.handler = handler;
        width = Config.WIDTH;
        height = Config.HEIGHT;
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
        this.g = g;
        fill(Config.BACKGROUND);

        // bodies
        for (Body b : world.getBodies()) {
            drawBody(b);
        }
        g.dispose();
    }

    private void drawWeb(@NotNull Graphics g, @NotNull Web w) {


    }

    private void drawBody(@NotNull Body b) {
        g.setColor(Config.EDGES);
        for (Edge e : b.getEdges()) {
            g.drawLine((int) e.getEdge1().getX(), (int) e.getEdge1().getY(), (int) e.getEdge2().getX(), (int) e.getEdge2().getY());
        }
        g.setColor(Config.POINTS);
        for (VPoint p : b.getPoints()) {
            g.fillRect((int) p.getX() - 3, (int) p.getY() - 3, 6, 6);
        }
    }

    public void drawImage(@NotNull Graphics g, @NotNull TurtleMenu menu) {
        this.g = g;
        if (!(handler.getState() == GameState.playClient || handler.getState() == GameState.playServer)) {
            fill(Config.menuBackground);
        }
        for (IElement e : menu.getElements()) {
            if (e instanceof GButton) {
                drawButton((GButton) e);
            } else if (e instanceof ServerList) {
                for (GButton b : ((ServerList) e).getButtons()) {
                    drawButton(b);
                }
            } else if (e instanceof PlayerList) {
                for (Label l : ((PlayerList) e).getLabels()) {
                    drawLabel(l);
                }
            }
        }
        g.dispose();
    }

    private void drawButton(@NotNull GButton button) {
        g.setColor(Color.ORANGE);
        g.drawRect(button.x, button.y, button.width, button.height);
        g.drawString(button.text, button.x + button.width / 2, button.y + button.height / 2);
    }

    private void drawLabel(@NotNull Label label) {
        g.setColor(Color.magenta);
        g.drawRect(label.x, label.y, label.width, label.height);
        g.drawString(label.text, label.x + label.width / 2, label.y + label.height / 2);
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    private void fill(Color c) {
        g.setColor(c);
        g.fillRect(0, 0, width, height);
    }
}

