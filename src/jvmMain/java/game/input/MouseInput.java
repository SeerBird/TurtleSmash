package game.input;

import game.output.GameWindow;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseInput extends MouseAdapter {
    GameWindow win;
    public static final int LEFT = 1;
    public static final int MID = 2;
    public static final int RIGHT = 3;

    public MouseInput(GameWindow win) {
        this.win = win;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //win.getHandler().postMouseEvent(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        win.getHandler().postMousePressEvent(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        win.getHandler().postMouseReleaseEvent(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        win.getHandler().postMouseMoveEvent(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        win.getHandler().postMouseMoveEvent(e);
    }
}
