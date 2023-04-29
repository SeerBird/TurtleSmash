package game.connection;

import java.util.ArrayList;

public class Packet {
    public enum Markers{
        Start,
        End,
    }
    public ArrayList<Markers> markers;
}
