package game.connection.packets.containers.images.edges;

import game.world.BPoint;
import game.world.World;
import game.world.bodies.Body;
import game.world.constraints.Edge;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;

public class BodyEdgePointer implements EdgeImage{
    @Serial
    private static final long serialVersionUID = 8008501;
    public int bi;
    public int i1;
    public int i2;

    public BodyEdgePointer(@NotNull Edge e) {
        Body parent = e.getEdge1().getParentBody();
        bi = World.getBodies().indexOf(parent);
        i1 = parent.getPoints().indexOf(e.getEdge1());
        i2 = parent.getPoints().indexOf(e.getEdge2());
    }
    public Edge findEdge(){
        Body parent = World.getBodies().get(bi);
        BPoint p1=parent.getPoints().get(i1);
        BPoint p2=parent.getPoints().get(i2);
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
