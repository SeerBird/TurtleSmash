package game.connection;

import game.world.World;
import game.world.bodies.Box;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Packet {//unnecessary?

    public enum Markers {
        Start,
        End,
    }

    public static ArrayList<Class> usedClasses = new ArrayList<>(Arrays.asList(ClientPacket.class, ServerPacket.class,
            InputInfo.class, World.class, ArrayList.class, Box.class, ArrayRealVector.class,double[].class,
            Color.class));
}
