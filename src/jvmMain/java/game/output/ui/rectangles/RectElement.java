package game.output.ui.rectangles;

import game.output.ui.IElement;

public abstract class RectElement extends IElement {
    public int width;
    public int height;

    public RectElement(int x, int y, int width, int height) {
        super(x, y);
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean press(double x, double y) {
        return x>this.x&&x<this.x+width&&y>this.y&&y<this.y+height;
    }
}
