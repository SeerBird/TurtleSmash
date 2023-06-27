package game.input;

import org.apache.commons.math3.linear.ArrayRealVector;

public class InputInfo {
    public ArrayRealVector mousepos;
    public boolean teleport=false;//make boolean and move to mousepos because redundancy
    public boolean create = false;
    public boolean webFling=false;
    public InputInfo(){
    }
    public void create(){
        create=true;
    }
    public void teleport(){
        teleport=true;
    }
    public void webFling(){
        webFling=true;
    }
    public void reset() {
        teleport =false;
        create = false;
        webFling=false;
    }
}
