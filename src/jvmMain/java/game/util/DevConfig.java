package game.util;

import java.awt.*;

public class DevConfig {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;
    public static Color BACKGROUND = new Color(31, 0, 136, 255);
    public static Color POINTS = new Color(0, 30, 234, 255);
    public static Color EDGES = new Color(196, 36, 0, 255);
    public static Color menuBackground = new Color(12, 11, 0, 61);
    public static Color shell = new Color(255, 166, 0, 255);
    public static Color web = new Color(255, 236, 177, 255);
    public static Color turtle = new Color(96, 183, 0, 255);
    public static int webLengthLimit = 20;
    public static double webRestNodeDistance = 14;
    public static double webFling = 24;
    public static double noReturnTime = 10; //seconds
    public static long discoveryMilliTimeout = 1300;
    public static long multicastMilliPeriod = 1000;
    public static double turtleSize = 1.0;
    public static double turtleDeformThreshold = 5.0;
    public static double turtleMass = 4.0;
    public static double shellMergeThreshold = 10.0;
    public static double shellStrapExtensionLimit = 6.0;
    public static double shellMass = 8.0;
    public static double playerSpawnSpread = 80;
    public static double approxPlayerSpawnVelocity = 10;
    public static double playerSpawnVelocitySpread = 2;
    public static int deathFrames = 120;
    public static int webTensileStrength = 10;
    public static double webDecayRate = 0.1;
    public static int turtleNakedFrames = 3;
    public static double constraintTolerance = 0.0;
    public static int maxNameLength = 20;
    public static double charWidth = 4;
    public static int maxAnimations = 200;
}
