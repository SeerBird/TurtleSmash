package game.connection;

import org.apache.commons.math3.linear.ArrayRealVector;

public class InputInfo {
    public ArrayRealVector webFling;
    public InputInfo(){
    }

    public void reset() {
        webFling=null;
    }
}
