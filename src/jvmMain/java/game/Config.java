package game;

import java.awt.*;

public class Config {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;
    public static final int KEY_COUNT = 256;
    public static final int TILE_SIZE = 24;
    public static final int CAMERA_DISTANCE = 24;
    public static final int CRASH_FRAME_DELAY = 600;
    public static Color BACKGROUND = new Color(64, 128, 0, 255);
    public static Color POINTS = new Color(0, 30, 234, 255);
    public static Color EDGES = new Color(196, 36, 0, 255);
    public static Color menuBackground = new Color(12, 11, 0, 61);
    public static int stringLengthLimit = 20;
    public static double stringRestNodeDistance = 14;
    public static double stringFling = 24;
    public static double noReturnTime = 10; //seconds
    public static long discoveryMilliTimeout = 3000;
    public static long multicastMilliPeriod = 1000;
    public static double turtleSize = 1.0;
    public static double turtleDeformThreshold = 2.0;
    public static double shellMergeThreshold = 3.0;
    public static double shellStrapExtensionLimit = 6.0;
    public static double shellMass = 8.0;
    public static double playerSpawnSpread = 80;
    public static double approxPlayerSpawnVelocity = 20;
    public static double playerSpawnVelocitySpread = 20;
    public static int deathFrames = 600;
}
