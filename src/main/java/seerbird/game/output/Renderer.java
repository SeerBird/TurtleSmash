package seerbird.game.output;


import javafx.util.Pair;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import seerbird.game.Config;
import seerbird.game.EventManager;
import seerbird.game.output.ui.IElement;
import seerbird.game.output.ui.Menu;
import seerbird.game.world.TurtleBody;
import seerbird.game.world.Web;
import seerbird.game.world.World;

import java.awt.*;
import java.awt.geom.Line2D;
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
        for (TurtleBody b : world.getBodies()) {
            bodyImage = b.getImage();
            g.drawImage(bodyImage, (int) b.getPos().getEntry(0) - bodyImage.getWidth() / 2,
                    (int) b.getPos().getEntry(1) - bodyImage.getHeight() / 2, null);
        }
        g.dispose();
    }

    private void drawWeb(@NotNull Graphics g, @NotNull Web w) {
        g.setColor(Color.red);
        g.fillRect((int) (w.getLinks().get(0).getKey().getEntry(0)), (int) (w.getLinks().get(0).getKey().getEntry(1)), 5, 5);
        g.setColor(Color.white);
        for (int i = 1; i < w.getLinks().size(); i++) {
            g.fillRect((int) (w.getLinks().get(i).getKey().getEntry(0)), (int) (w.getLinks().get(i).getKey().getEntry(1)), 3, 3);
            if (handler.getWorld().getDistance((int) w.getLinks().get(i - 1).getKey().getEntry(0), (int) w.getLinks().get(i - 1).getKey().getEntry(1),
                    (int) w.getLinks().get(i).getKey().getEntry(0), (int) w.getLinks().get(i).getKey().getEntry(1)) < Config.WIDTH / 2.0) {
                g.drawLine((int) w.getLinks().get(i - 1).getKey().getEntry(0), (int) w.getLinks().get(i - 1).getKey().getEntry(1),
                        (int) w.getLinks().get(i).getKey().getEntry(0), (int) w.getLinks().get(i).getKey().getEntry(1));
            }


        }
        if (w.getConnected() != null) {
            g.drawLine((int) w.getStickyPos().getEntry(0), (int) w.getStickyPos().getEntry(1),
                    (int) w.getConnected().getX(), (int) w.getConnected().getY());
        }
        if (w.getSource() != null) {
            g.drawLine((int) w.getFirstPos().getEntry(0), (int) w.getFirstPos().getEntry(1),
                (int) w.getSource().getX(), (int) w.getSource().getY());
        }
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

