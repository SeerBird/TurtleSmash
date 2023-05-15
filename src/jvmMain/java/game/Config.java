package game;

import java.awt.*;

public class Config {
    public static final int TCPPort=5455;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;
    public static final int KEY_COUNT = 256;
    public static final int TILE_SIZE = 24;
    public static final int CAMERA_DISTANCE = 24;
    public static final int CRASH_FRAME_DELAY = 600;
    public static Color BACKGROUND = new Color(64, 128, 0, 255);
    public static Color POINTS = new Color(0, 30, 234, 255);
    public static Color EDGES = new Color(196, 36, 0, 255);
    public static double turtleMass = 50;
    public static double shellMass = 3;
    public static int stringLimit = 3;
    public static int stringLengthLimit = 20;
    public static double stringTensileStrength = 0.5;
    public static double stringRestNodeDistance = 14;
    public static double gravity = 1;
    public static double stringFling = 24;
    public static double noReturnTime = 10; //seconds
    public static Color menuBackground =new Color(12, 11, 0, 61);
    public static long discoveryMilliTimeout = 3000000;
}
