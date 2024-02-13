package game.output;


import game.GameHandler;
import game.GameState;
import game.output.animations.Animation;
import game.output.ui.IElement;
import game.output.ui.TurtleMenu;
import game.output.ui.rectangles.Button;
import game.output.ui.rectangles.Label;
import game.output.ui.rectangles.*;
import game.util.DevConfig;
import game.world.BPoint;
import game.world.World;
import game.world.bodies.Body;
import game.world.bodies.Shell;
import game.world.bodies.Turtle;
import game.world.bodies.Web;
import game.world.constraints.Edge;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;

public class Renderer {
    static Graphics g;
    static final ArrayList<Animation> animations = new ArrayList<>();
    static int x = 0;
    static int y = 0;

    public static void update() { //get new info and progress animations
        for (Animation animation : new ArrayList<>(animations)) {
            if (!animation.drawNext(g)) {
                removeAnimation(animation);
            }
        }
    }

    public static void drawImage(Graphics g) {
        Renderer.g = g;
        g.translate(x, y);
        fill(DevConfig.BACKGROUND);
        update();
        drawWorld();
        drawMenu();
        g.dispose();
    }

    //region World
    private static void drawWorld() { // get all the visible objects, effects, and particles on an image
        for (Body b : World.getBodies()) {
            drawBody(b);
        }
    }

    private static void drawBody(@NotNull Body b) {
        //region Shells
        if (b instanceof Shell) {
            ArrayList<BPoint> points = b.getPoints();
            int n = points.size();
            int[] x = new int[n];
            int[] y = new int[n];
            for (int i = 0; i < points.size(); i++) {
                x[i] = (int) points.get(i).getX();
                y[i] = (int) points.get(i).getY();
            }
            g.setColor(DevConfig.shell);
            g.fillPolygon(x, y, n);
        }
        //endregion
        //region Turtles
        else if (b instanceof Turtle) {
            ArrayList<BPoint> points = b.getPoints();
            int n = points.size();
            int[] x = new int[n];
            int[] y = new int[n];
            for (int i = 0; i < points.size(); i++) {
                x[i] = (int) points.get(i).getX();
                y[i] = (int) points.get(i).getY();
            }
            g.setColor(DevConfig.turtle);
            g.fillPolygon(x, y, n);
            if (GameHandler.isHost(((Turtle) b).owner)) {
                g.setColor(DevConfig.HIGHLIGHT);
                g.drawPolygon(x, y, n);
            }
        }
        //endregion
        //region Webs
        else if (b instanceof Web) {
            g.setColor(DevConfig.web);
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
            if (((Web) b).sourceEdge != null) {
                Edge e = ((Web) b).sourceEdge;
                g.drawLine((int) e.getEdge1().getX(), (int) e.getEdge1().getY(), (int) e.getEdge2().getX(), (int) e.getEdge2().getY());
            }
        }
        //endregion
        //region show that something cursed has happened
        else {
            g.setColor(DevConfig.CURSED);
            for (BPoint p : b.getPoints()) {
                g.fillRect((int) p.getX() - 3, (int) p.getY() - 3, 6, 6);
            }
        }
        //endregion
    }
    //endregion

    //region Menu
    private static void drawMenu() {
        if (!(GameHandler.getState() == GameState.playClient || GameHandler.getState() == GameState.playServer)) {
            fill(DevConfig.menuBackground);
        }
        for (IElement e : TurtleMenu.getElements()) {
            if (e instanceof Button) {
                drawButton((Button) e);
            } else if (e instanceof ServerList) {
                for (Button b : ((ServerList) e).getButtonServers()) {
                    drawButton(b);
                }
                drawLabel(((ServerList) e).title);
            } else if (e instanceof PlayerList) {
                for (Label l : ((PlayerList) e).getLabels()) {
                    drawLabel(l);
                }
                drawRect((RectElement) e, DevConfig.turtle);
            } else if (e instanceof Scoreboard) {
                if (((Scoreboard) e).visible) {
                    for (Label p : ((Scoreboard) e).getScores().keySet()) {
                        drawLabel(p);
                    }
                    for (Label p : ((Scoreboard) e).getScores().values()) {
                        drawLabel(p);
                    }
                }
            } else if (e instanceof Textbox) {
                drawTextbox((Textbox) e);
            } else if (e instanceof Toggleable) {
                drawToggleable((Toggleable) e);
            } else if (e instanceof Label) {
                drawLabel((Label) e);
            }
        }
    }

    //region draw elements
    private static void drawRect(@NotNull RectElement e, Color color) {
        g.setColor(color);
        g.drawRect(e.x, e.y, e.width, e.height);
    }


    private static void drawButton(@NotNull Button button) {
        if (button.isPressed()) {
            g.setColor(button.textColor.darker());
        } else {
            g.setColor(button.textColor);
        }
        g.drawRect(button.x, button.y, button.width, button.height);

        g.drawRect(button.x + 4, button.y + 4, button.width - 8, button.height - 8);
        drawLabelText(button, button.textColor);
    }

    private static void drawToggleable(@NotNull Toggleable toggle) {
        if (toggle.getState()) {
            g.setColor(toggle.textColor.darker());
        } else {
            g.setColor(toggle.textColor);
        }
        g.drawRect(toggle.x, toggle.y, toggle.width, toggle.height);
        g.drawRect(toggle.x + 4, toggle.y + 4, toggle.width - 8, toggle.height - 8);
        drawLabelText(toggle, toggle.textColor);
    }

    private static void drawLabel(@NotNull Label label) {
        drawLabelText(label, label.textColor);
    }

    private static void drawTextbox(@NotNull Textbox textbox) {
        drawLabelText(textbox, textbox.textColor);
        drawRect(textbox, textbox.textColor);
    }

    private static void drawLabelText(@NotNull Label label, Color color) {
        g.setColor(color);
        g.drawString(label.text, label.x + label.width / 2 - g.getFontMetrics().stringWidth(label.text) / 2, label.y + label.height / 2);
    }

    //endregion
    //endregion

    //region Animations
    public static Animation addAnimation(Animation animation) {
        animations.add(animation);
        return animation;
    }

    public static void removeAnimation(Animation animation) {
        animations.remove(animation);
    }
    //endregion


    private static void fill(Color c) {
        g.setColor(c);
        g.fillRect(-200, -200, DevConfig.WIDTH + 400, DevConfig.HEIGHT + 400);
    }

    public static int getStringWidth(String string) {
        if (g != null) {
            return g.getFontMetrics().stringWidth(string);
        } else {
            return -1;
        }
    }

    public static void setPos(@NotNull ArrayRealVector p) {
        x = (int) p.getEntry(0);
        y = (int) p.getEntry(1);
    }
}

