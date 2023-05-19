package game.input;

import game.EventManager;
import game.output.GameWindow;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseInput extends MouseAdapter {
    EventManager handler;
    public static final int LEFT = 1;
    public static final int MID = 2;
    public static final int RIGHT = 3;

    public MouseInput(EventManager handler) {
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
