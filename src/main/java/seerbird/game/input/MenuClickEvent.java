package seerbird.game.input;

import seerbird.game.output.ui.IElement;

import java.awt.*;

public class MenuClickEvent extends AWTEvent {
    public MenuClickEvent(IElement source, int id) {
        super(source, id);
    }
}
