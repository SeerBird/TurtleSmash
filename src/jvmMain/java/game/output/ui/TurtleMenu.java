package game.output.ui;

import game.EventManager;
import game.input.MenuClickEvent;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class TurtleMenu {
    private ArrayList<IElement> elements;
    private IElement pressed;
    EventManager handler;

    public TurtleMenu(EventManager handler) {
        this.handler = handler;
        elements = new ArrayList<IElement>();

        // Add elements
        elements.add(new Button(200, 200, () -> System.out.println("Maboi")));
    }

    public void press(float x, float y) {
        for (IElement element : elements) {
            if (element.press(x, y)) {
                handler.postMenuClickEvent(new MenuClickEvent(element, ActionEvent.ACTION_PERFORMED));
                pressed = element;
                break;
            }
        }
    }

    public void release() {
        if (pressed != null) {
            pressed.release();
        }
    }

    public void update() {
        for (IElement element : elements) {
            if (element instanceof Button) {
                ((Button) element).move();
            }
        }
    }

    public ArrayList<IElement> getElements() {
        return elements;
    }

    public Button getElement(int i) {
        IElement butt = elements.get(i);
        if(butt instanceof Button) {
            return (Button)butt;
        }
        return null;
    }
}
