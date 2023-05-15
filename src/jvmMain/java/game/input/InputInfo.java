package game.input;

import org.apache.commons.math3.linear.ArrayRealVector;

public class InputInfo {
    public ArrayRealVector teleport;
    public InputInfo(){
    }

    public void reset() {
        teleport =null;
    }
}
