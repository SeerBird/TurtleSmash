package game.connection.packets.containers.images;

import game.world.VPoint;
import game.world.World;
import game.world.bodies.Body;
import game.world.constraints.Edge;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BodyEdgeImage {
    public int bodyindex;
    public int index1;
    public int index2;

    public BodyEdgeImage(@NotNull Edge e) {
        Body parent = e.getEdge1().getParentBody();
        bodyindex = parent.getParentWorld().getBodies().indexOf(parent);
        index1 = parent.getPoints().indexOf(e.getEdge1());
        index2 = parent.getPoints().indexOf(e.getEdge2());
    }
    public Edge findEdge(@NotNull World world){
        Body parent = world.getBodies().get(bodyindex);
        VPoint p1=parent.getPoints().get(index1);
        VPoint p2=parent.getPoints().get(index2);
        for(Edge e: parent.getEdges()){
            if(e.getEdge1()==p1){
                if(e.getEdge2()==p2){
                    return e;
                }
            }
        }
        throw new RuntimeException("Image to body restoration failure. Why couldn't I find the edge? I don't get it... " +
                "Don't do this before the edge is created!");
    }
}
