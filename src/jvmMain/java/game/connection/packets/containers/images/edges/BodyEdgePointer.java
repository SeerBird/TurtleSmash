package game.connection.packets.containers.images.edges;

import game.world.BPoint;
import game.world.World;
import game.world.bodies.Body;
import game.world.constraints.Edge;
import org.jetbrains.annotations.NotNull;

public class BodyEdgePointer implements EdgeImage{
    public int bodyindex;
    public int index1;
    public int index2;

    public BodyEdgePointer(@NotNull Edge e) {
        Body parent = e.getEdge1().getParentBody();
        bodyindex = World.getBodies().indexOf(parent);
        index1 = parent.getPoints().indexOf(e.getEdge1());
        index2 = parent.getPoints().indexOf(e.getEdge2());
    }
    public Edge getEdge(){
        Body parent = World.getBodies().get(bodyindex);
        BPoint p1=parent.getPoints().get(index1);
        BPoint p2=parent.getPoints().get(index2);
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
