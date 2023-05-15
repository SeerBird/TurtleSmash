package game.connection.packets;

import game.world.VPoint;
import game.world.World;
import game.world.bodies.Body;
import game.world.bodies.Box;
import game.world.constraints.DistanceConstraint;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Packet implements Serializable{//unnecessary?

    public enum Markers {
        Start,
        End,
    }
    /*
    public static ArrayList<Class> usedClasses = new ArrayList<>(Arrays.asList(ClientPacket.class, ServerPacket.class,
            InputInfo.class, World.class, ArrayList.class, Box.class, ArrayRealVector.class,double[].class,
            Color.class,float[].class, DistanceConstraint.class, VPoint.class, Body.class));

     */
}
