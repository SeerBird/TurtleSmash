package seerbird.game.input;

import seerbird.game.output.GameWindow;

import javax.swing.event.MouseInputListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseInput extends MouseAdapter{
    GameWindow win;

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
