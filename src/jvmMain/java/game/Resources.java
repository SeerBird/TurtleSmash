package game;

import java.net.URL;

public class Resources {
    public static final URL goodnight;
    String playerName;
    static {
        goodnight = Resources.class.getResource("goodnight.wav");
    }
}
