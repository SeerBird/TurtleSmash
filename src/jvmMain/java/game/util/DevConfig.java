package game.util;

import java.awt.*;

public class DevConfig {
    public static final int WIDTH = 1540;
    public static final int HEIGHT = 787;
    public static Color BACKGROUND = new Color(31, 0, 136, 255);
    public static Color CURSED = new Color(0, 30, 234, 255);
    public static Color HIGHLIGHT = new Color(196, 36, 0, 255);
    public static Color menuBackground = new Color(12, 11, 0, 61);
    public static Color shell = new Color(255, 166, 0, 255);
    public static Color web = new Color(255, 236, 177, 255);
    public static Color turtle = new Color(96, 183, 0, 255);
    public static int webLengthLimit = 35;
    public static double webRestNodeDistance = 14;
    public static double webFling = 24;
    public static double noReturnTime = 10; //seconds
    public static long discoveryMilliTimeout = 1300;
    public static long multicastMilliPeriod = 1000;
    public static double turtleSize = 1.0;
    public static double turtleDeformThreshold = 5.0;
    public static double turtleMass = 4.0;
    public static double shellMergeThreshold = 10.0;
    public static double shellStrapExtensionLimit = 1.5;
    public static double shellMass = 4.0;
    public static double playerSpawnSpread = 80;
    public static double approxPlayerSpawnVelocity = 6;
    public static double playerSpawnVelocitySpread = 1.5;
    public static int deathFrames = 120;
    public static int webTensileStrength = 10;
    public static double webDecayRate = 0.1;
    public static int turtleNakedFrames = 3;
    public static double constraintTolerance = 0.0;
    public static int maxNameLength = 30;
    public static int maxAnimations = 200;
    public static double recoil = 12.0;
    public static double gravity = 50;
    public static double minGravityRadius = 80;
    public static double shakeDecay = 0.9;
    public static double shakeIntensity = 1.0;
    public static double burstIntensity = 0.008;
    public static double particleLingerFrames = 20;
    public static int shellSnapFlashFrames = 50;
}
