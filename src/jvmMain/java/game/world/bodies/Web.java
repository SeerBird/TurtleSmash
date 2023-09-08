package game.world.bodies;

import game.Config;
import game.world.CollisionData;
import game.world.VPoint;
import game.world.World;
import game.world.constraints.Edge;
import game.world.constraints.WebStick;
import javafx.util.Pair;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Web extends Body {
    VPoint source;
    boolean attached;
    boolean isGrowing;
    Map<VPoint, ArrayList<WebStick>> stickies;
    ArrayList<WebStick> toUnstick;

    // last point is attached to source
    public Web(@NotNull World world, @NotNull VPoint source, ArrayRealVector velocity) {
        super(world);
        stickies = new HashMap<>();
        toUnstick = new ArrayList<>();
        this.source = source;
        addPoint(new VPoint(this, 1, source.getPos()));
        points.get(0).accelerate(velocity);
        attached = true;
        isGrowing = true;
    }

    @Override
    public ArrayList<Edge> getSides() {// do not return sides that are stuck
        ArrayList<Edge> sides = new ArrayList<>();
        for (int i = 0; i < points.size() - 1; i++) {
            if (!(notStuck(i) || notStuck(i + 1))) {
                sides.add(edges.get(i));
            }
        }
        return sides;
    }

    private boolean notStuck(int pointID) {
        return stickies.get(points.get(pointID)).size() == 0;
    }

    @Override
    public void addEdge(VPoint p1, VPoint p2) {
        super.addEdge(p1, p2);
    }

    @Override
    public void addPoint(VPoint p) {
        super.addPoint(p);
        stickies.put(p, new ArrayList<>());
    }

    @Override
    public void move() {
        super.move();
        if (attached) {
            if (isGrowing) {
                ArrayRealVector root = points.get(points.size() - 1).getPos();
                ArrayRealVector dist = points.get(points.size() - 1).getDistance(source);
                double distance = dist.getNorm() / Config.stringRestNodeDistance; // not in pixels but in rest distances
                if (distance > 1) {
                    if (points.size() >= Config.stringLengthLimit) {
                        fix();
                    }// stop growth when limit is reached
                    else {
                        dist.mapMultiplyToSelf(1 / distance); // get a rest distance in the right direction
                        for (int i = 1; i < distance; i++) {
                            addPoint(new VPoint(this, 1, root.combineToSelf(1, 1, dist)));
                            addEdge(points.get(points.size() - 2), points.get(points.size() - 1));
                        }
                    }
                }// grow
            } else {
                // what does it do when attached but not growing? just moves? maybe collapse ifs
            }
        }// still attached
        else {
            // eh idk. space for content ig
        }
    }

    @Override
    public ArrayList<Pair<Double, VPoint>> project(@NotNull ArrayRealVector axis) {
        double norm = axis.getNorm();
        if (norm != 1.0) {
            axis.mapMultiplyToSelf(1 / norm);
        }
        VPoint minp = points.get(0);
        VPoint maxp = points.get(0);
        double min = axis.dotProduct(minp.getPos());
        double max = min;
        double projection;
        VPoint p;
        for (int i = 0; i < points.size(); i++) {//doing the first point over, idc
            if (notStuck(i)) {
                p = points.get(i);
                projection = axis.dotProduct(p.getPos());
                if (projection > max) {
                    max = projection;
                    maxp = p;
                } else if (projection < min) {
                    min = projection;
                    minp = p;
                }
            }
        }
        ArrayList<Pair<Double, VPoint>> res = new ArrayList<>(); // I should probably change this to a pair
        res.add(new Pair<>(min, minp));
        res.add(new Pair<>(max, maxp));
        return res;
    }

    @Override
    public void collide(@NotNull CollisionData collision) {
        if (collision.getVertex().getParentBody() == this) { //only the vertices are sticky
            if (points.contains(collision.getVertex())) {
                int i = points.indexOf(collision.getVertex());
                if (i == 0) {
                    if (intersect(edges.get(i), collision.getEdge())) {
                        stick(collision.getVertex(), collision.getEdge());
                    }
                } else if (i == points.size() - 1) {
                    if (intersect(edges.get(i - 1), collision.getEdge())) {
                        stick(collision.getVertex(), collision.getEdge());
                    }
                } else {
                    if (intersect(edges.get(i), collision.getEdge()) || intersect(edges.get(i - 1), collision.getEdge())) {
                        stick(collision.getVertex(), collision.getEdge());
                    }
                }
            }
        }
    }

    @Override
    public boolean constrain() {
        boolean edges = super.constrain();
        boolean stickySat = true;
        for (ArrayList<WebStick> connections : stickies.values()) {
            for (WebStick sticky : connections) {
                stickySat &= sticky.satisfy();
            }
        }
        for (WebStick stick : toUnstick) {
            stickies.get(stick.getEdge1()).remove(stick);
        }
        toUnstick.clear();
        return edges & stickySat;
    }

    public void unstick(@NotNull WebStick webStick) {
        toUnstick.add(webStick);
    }

    private void stick(@NotNull VPoint webPoint, @NotNull Edge edge) {//create a new one-sided distance constraint class?
        ArrayRealVector distance = edge.getEdge1().getDistance(edge.getEdge2());
        double edgeX = distance.getEntry(0);
        double edgeY = distance.getEntry(1);
        // should be 0 to 1, indicating where between edge1 and edge2 the vertex projection is
        double placement = (distance.getNorm() > 0) ? (Math.abs(edgeX) >= Math.abs(edgeY)) ? (webPoint.getX() - edge.getEdge1().getX()) / (edgeX) : (webPoint.getY() - edge.getEdge1().getY()) / (edgeY) : 0.5;
        webPoint.setPos(edge.getEdge1().getPos().combine(1, 1, distance.mapMultiply(placement)));
        stickies.get(webPoint).add(new WebStick(webPoint, edge.getEdge1()));
        stickies.get(webPoint).add(new WebStick(webPoint, edge.getEdge2()));
        int stuckEnd = points.indexOf(webPoint) + 1;
        int stuckStart = stuckEnd - 2;
        for (; stuckEnd < points.size(); stuckEnd++) {
            if (notStuck(stuckEnd)) {
                break;
            }
        }
        stuckEnd--;
        for (; stuckStart >= 0; stuckStart--) {
            if (notStuck(stuckStart)) {
                break;
            }
        }
        stuckStart++;
        if (stuckEnd - stuckStart >= 2 && stuckStart >= 0 && stuckEnd < points.size()) {
            for (int i = stuckEnd - 1; i > stuckStart; i--) {//descending to work with list.remove
                VPoint removed = points.get(i);
                points.remove(removed);
                stickies.remove(removed);
                edges.remove(i);
            }
            edges.remove(stuckStart);
            edges.add(stuckStart, new Edge(points.get(stuckStart), points.get(stuckStart + 1)));
        }
    }

    public boolean isGrowing() {
        return isGrowing;
    }

    public void fix() {
        if (isGrowing) {
            isGrowing = false;
            addEdge(points.get(points.size() - 1), source);
        }
    }

    @Override
    public boolean gravitates() {
        return false;
    }

    public void keepGrowing() {
        if (!isGrowing) {
            isGrowing = true;
            edges.remove(edges.size() - 1);
        }
    }

    public void detach() {
        if (attached) {
            if (!isGrowing) {
                edges.remove(edges.size() - 1);
            }
            attached = false;
        }
    }
}
