package seerbird.game;

import java.awt.*;

public class Config {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;
    public static final int KEY_COUNT = 256;
    public static final int TILE_SIZE = 24;
    public static final int CAMERA_DISTANCE = 24;
    public static final int CRASH_FRAME_DELAY = 600;
    public static Color BACKGROUND = new Color(65, 130, 65, 200);
    public static double turtleMass = 50;
    public static double shellMass = 3;
    public static double turtleCollisionElasticity = 1;
    public static double shellCollisionElasticity = 1;
    public static double turtleShellCollisionElasticity = 1;
    public static int stringLimit = 3;
    public static int stringLengthLimit = 160;
    public static double stringTensileStrength = 0.5;
    public static double stringRestNodeDistance = 2;
    public static double gravity = 10;
    public static double stringFling = 24;
    public static double minGravityDistance = 8;
}
