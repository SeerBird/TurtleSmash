package seerbird.game.world;

import javafx.util.Pair;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import seerbird.game.Config;

import java.util.ArrayList;

public class Web {
    // nodes have mass 1, disregarded
    ArrayList<Pair<ArrayRealVector, ArrayRealVector>> links; // <pos, velocity> first position is sticky
    Turtle source;
    TurtleBody connected;
    World world;

    public Web(@NotNull Turtle source, @NotNull ArrayRealVector velocity) {
        this.source = source;
        connected = null;
        links = new ArrayList<>();
        links.add(new Pair<>(source.getPos().copy(), velocity.copy().combineToSelf(1, 1, source.getVelocity())));
        world = source.getWorld();
    }

    public ArrayList<Pair<ArrayRealVector, ArrayRealVector>> getLinks() {
        return this.links;
    }

    private ArrayRealVector getLinkPos(int index) {
        return this.links.get(index).getKey().copy();
    }

    public TurtleBody getConnected() {
        return connected;
    }

    public TurtleBody getSource() {
        return source;
    }

    public ArrayRealVector getStickyPos() {
        return this.links.get(0).getKey().copy();
    }
    public ArrayRealVector getFirstPos() {
        return this.links.get(links.size()-1).getKey().copy();
    }

    private ArrayRealVector getLinkVelocity(int index) {
        return this.links.get(index).getValue().copy();
    }

    private double getTension(double distance) {// tension based on the distance
        return (distance - Config.stringRestNodeDistance) * -Config.stringTensileStrength;
    }

    public void update() {
        ArrayRealVector dist;
        double t;
        for (Pair<ArrayRealVector, ArrayRealVector> link : links) {
            world.borderLink(link.getKey().combineToSelf(1, 1, link.getValue().mapMultiplyToSelf(0.999)));
        }//move
        for (int i = 0; i < links.size() - 1; i++) {
            dist = world.getBorderDistance(links.get(i).getKey(), links.get(i + 1).getKey());
            t = getTension(dist.getNorm());
            links.get(i).getValue().combineToSelf(1, t / dist.getNorm(), dist);
            links.get(i + 1).getValue().combineToSelf(1, -t / dist.getNorm(), dist);
        }//tension in the string
        if (source != null) {
            {
                dist = world.getBorderDistance(getLinkPos(links.size() - 1), source.getPos());
                t = getTension(dist.getNorm());
                source.accelerateMass((ArrayRealVector) dist.mapMultiply(-t / dist.getNorm()));
                links.get(links.size() - 1).getValue().combineToSelf(1, t / dist.getNorm(), dist);
            }//tension with source
            if (connected == null) {
                {
                    connected = world.turtleBodyIntersects(getStickyPos());
                    if (connected == source) {
                        connected = null;
                    }
                }//stick
                if (links.size() < Config.stringLengthLimit) {
                    //last created node to source
                    dist = world.getBorderDistance(source.getStringSource(), getLinkPos(links.size() - 1));
                    int newNodes = (int) (dist.getNorm() / Config.stringRestNodeDistance); // is cast the same as floor?
                    dist.mapMultiplyToSelf(Config.stringRestNodeDistance / dist.getNorm());// get normalized vector
                    for (int i = 1; i <= newNodes; i++) {
                        links.add(new Pair<>(getLinkPos(links.size() - 1).combine(1, 1, dist), getLinkVelocity(links.size() - 1)));
                    }
                }// create new links so that there is nothing pulling on the sticky one else
                else {
                    source.getWorld().getHandler().postStringFallOff(this);
                    source = null;
                }//fall off
            } else {
                dist = getLinkPos(0).combine(-1, 1, connected.getPos());
                t = getTension(dist.getNorm());
                connected.accelerateMass((ArrayRealVector) dist.mapMultiply(t / dist.getNorm()));
                links.get(0).getValue().combineToSelf(1, -t / dist.getNorm(), dist);
            }//tension with connected body
        } else {
            //get deaded
        }
    }
}
