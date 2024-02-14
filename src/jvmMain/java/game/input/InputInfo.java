package game.input;

import org.apache.commons.math3.linear.ArrayRealVector;

import java.io.Serial;
import java.io.Serializable;

public class InputInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 4550;
    public ArrayRealVector mousepos;
    public boolean teleport = false;//make boolean and move to mousepos because redundancy
    public boolean create = false;
    public boolean webFling = false;
    public boolean detachWeb = false;

    public InputInfo() {
    }

    public void create() {
        create = true;
    }

    public void drag() {
        teleport = true;
    }

    public void webFling() {
        webFling = true;
    }

    public void reset() {
        teleport = false;
        create = false;
        webFling = false;
        detachWeb = false;
    }

    public void detachWeb() {
        detachWeb = true;
    }
}
