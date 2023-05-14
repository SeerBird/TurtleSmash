package game.output.ui.rectangles;


import org.jetbrains.annotations.NotNull;

public class Label extends RectElement{
    public String text;
    public Label(double x, double y, int maxwidth, int maxheight, @NotNull String text) {
        super(x,y,maxwidth,maxheight);
        this.text=text;
    }

    @Override
    public void release() {

    }
    public void setText(String text){
        this.text=text;
    }
}
