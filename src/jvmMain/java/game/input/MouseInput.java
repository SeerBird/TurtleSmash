package game.input;

import game.GameHandler;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class MouseInput extends MouseAdapter {
    GameHandler handler;
    private static final Map<Integer, MouseEvent> mousePressEvents = new HashMap<>();
    private static final Map<Integer, MouseEvent> mouseReleaseEvents = new HashMap<>();
    private MouseEvent mouseMoveEvent;
    private static final ArrayRealVector mousepos = new ArrayRealVector(2);
    public static final int LEFT = 1;
    public static final int MID = 2;
    public static final int RIGHT = 3;

    public MouseInput(GameHandler handler) {
        this.handler = handler;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //win.getHandler().postMouseEvent(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        handler.postMousePressEvent(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        handler.postMouseReleaseEvent(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        handler.postMouseMoveEvent(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        handler.postMouseMoveEvent(e);
    }
}
