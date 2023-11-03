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
import game.world.BPoint;
import game.world.World;
import game.world.bodies.Body;
import game.world.bodies.Shell;
import game.world.bodies.Turtle;
import game.world.bodies.Web;
import game.world.constraints.Edge;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;

public class Renderer {
    static int height = Config.HEIGHT;
    static int width = Config.WIDTH;
    static Graphics g;

    public void update() { //get new info and progress animations

    }

    public static void drawImage(Graphics g) {
        drawWorld(g);
        drawMenu(g);
        g.dispose();
    }

    private static void drawWorld(@NotNull Graphics g) { // get all the visible objects, effects, and particles on an image
        Renderer.g = g;
        fill(Config.BACKGROUND);

        // bodies
        for (Body b : World.getBodies()) {
            drawBody(b);
        }
    }

    private void drawWeb(@NotNull Graphics g, @NotNull Web w) {


    }

    private static void drawBody(@NotNull Body b) {
        //region Shells
        if (b instanceof Shell) {
            ArrayList<BPoint> points=b.getPoints();
            int n = points.size();
            int[] x = new int[n];
            int[] y = new int[n];
            for (int i=0;i< points.size();i++) {
                x[i]=(int)points.get(i).getX();
                y[i]=(int)points.get(i).getY();
            }
            g.setColor(Config.shell);
            g.fillPolygon(x,y,n);
        }
        //endregion
        //region Turtles
        else if (b instanceof Turtle) {
            ArrayList<BPoint> points=b.getPoints();
            int n = points.size();
            int[] x = new int[n];
            int[] y = new int[n];
            for (int i=0;i< points.size();i++) {
                x[i]=(int)points.get(i).getX();
                y[i]=(int)points.get(i).getY();
            }
            g.setColor(Config.turtle);
            g.fillPolygon(x,y,n);
        }
        //endregion
        //region Webs
        else if (b instanceof Web) {
            int i = 0;
            g.setColor(Config.web);
            for (BPoint p : b.getPoints()) {
                g.fillRect((int) p.getX() - 1, (int) p.getY() - 1, 2, 2);
            }
            for (Edge e : b.getEdges()) {
                g.drawLine((int) e.getEdge1().getX(), (int) e.getEdge1().getY(), (int) e.getEdge2().getX(), (int) e.getEdge2().getY());
            }
            if (((Web) b).targetEdge1 != null && ((Web) b).targetEdge2 != null) {
                Edge e = ((Web) b).targetEdge1;
                g.drawLine((int) e.getEdge1().getX(), (int) e.getEdge1().getY(), (int) e.getEdge2().getX(), (int) e.getEdge2().getY());
                e = ((Web) b).targetEdge2;
                g.drawLine((int) e.getEdge1().getX(), (int) e.getEdge1().getY(), (int) e.getEdge2().getX(), (int) e.getEdge2().getY());
            }
            if(((Web) b).sourceEdge!=null){
                Edge e = ((Web) b).sourceEdge;
                g.drawLine((int) e.getEdge1().getX(), (int) e.getEdge1().getY(), (int) e.getEdge2().getX(), (int) e.getEdge2().getY());
            }
        }
        //endregion
        else {
            g.setColor(Config.POINTS);
            for (BPoint p : b.getPoints()) {
                g.fillRect((int) p.getX() - 3, (int) p.getY() - 3, 6, 6);
            }
        }
    }

    private static void drawMenu(@NotNull Graphics g) {
        Renderer.g = g;
        if (!(GameHandler.getState() == GameState.playClient || GameHandler.getState() == GameState.playServer)) {
            fill(Config.menuBackground);
        }
        for (IElement e : TurtleMenu.getElements()) {
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
    }

    private static void drawButton(@NotNull GButton button) {
        g.setColor(Color.ORANGE);
        g.drawRect(button.x, button.y, button.width, button.height);
        g.drawString(button.text, button.x + button.width / 2, button.y + button.height / 2);
    }

    private static void drawLabel(@NotNull Label label) {
        g.setColor(Color.magenta);
        g.drawRect(label.x, label.y, label.width, label.height);
        g.drawString(label.text, label.x + label.width / 2, label.y + label.height / 2);
    }

    public static void resize(int width, int height) {
        Renderer.width = width;
        Renderer.height = height;
    }

    private static void fill(Color c) {
        g.setColor(c);
        g.fillRect(0, 0, width, height);
    }
    private static void drawEdge(@NotNull Edge e){
        g.drawLine((int) e.getEdge1().getX(), (int) e.getEdge1().getY(), (int) e.getEdge2().getX(), (int) e.getEdge2().getY());
    }
}

