package game.connection.packets.containers.images;

import game.world.bodies.Body;
import game.world.bodies.Web;
import game.world.constraints.Edge;

public class WebImage extends BodyImage {
    public BodyEdgeImage target;
    public WorldEdgeImage sourceEdge;
    public WorldEdgeImage targetEdge1;
    public WorldEdgeImage targetEdge2;
    public boolean isGrowing;

    public WebImage(Body body) {
        super(body);
        if (body.getClass() != Web.class) {
            throw new RuntimeException("How did you manage to try to make a web image of a non-web body. You idiot.");
        } else {
            Edge edge;
            Web web = (Web) body;
            if ((edge = web.getTarget()) != null) {
                target = new BodyEdgeImage(edge);
                targetEdge1 = new WorldEdgeImage(web.targetEdge1);
                targetEdge2 = new WorldEdgeImage(web.targetEdge2);
            }
            if ((edge = web.getSourceEdge()) != null) {
                sourceEdge = new WorldEdgeImage(edge);
            }
            isGrowing = web.isGrowing();
        }
    }

    @Override
    public Body getIsolatedBody() {
        Web web = (Web) super.getIsolatedBody();
        web.setGrowing(isGrowing);
        return web;
    }

    @Override
    public void connectBody(Body body) {
        Web web = (Web) body;
        if (target != null) {
            web.targetEdge1 = targetEdge1.getEdge();
            web.targetEdge2 = targetEdge2.getEdge();
            web.target = target.findEdge();
        }
        if (sourceEdge != null) {
            web.sourceEdge = sourceEdge.getEdge();
            web.source=web.sourceEdge.getEdge1();
        }
    }
}
