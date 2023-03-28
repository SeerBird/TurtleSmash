package seerbird.game.world;

import org.apache.commons.math3.linear.ArrayRealVector;
import seerbird.game.Config;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class Turtle extends TurtleBody {
    // move this to config?
    //mock
    static final Shape whole = new Area(new Rectangle(0, 0, 160, 160));
    static final Shape body = new Area(new Rectangle(0, 0, 160, 160));
    static final BufferedImage wholeImage = new BufferedImage(7, 7, 1);
    static final BufferedImage bodyImage = new BufferedImage(5, 7, 1);

    static {
        int side=100;
        Graphics g = wholeImage.getGraphics();
        g.setColor(Color.GREEN);
        g.fillRect(0, 0, side, side);
        g = bodyImage.getGraphics();
        g.setColor(Color.ORANGE);
        g.fillRect(0, 0, side, side);
        g.dispose();
    }
    //end mock

    boolean intact;

    public Turtle(World world, double x, double y) {
        super(world, Config.turtleMass + Config.shellMass, x, y);
        this.collisionElasticity = Config.turtleShellCollisionElasticity;
        this.shape=whole;
    }

    public ArrayRealVector getStringSource() {
        return this.pos;
    }

    public boolean smash() {
        if (intact) {
            intact = false;
            this.mass = Config.turtleMass;
            collisionElasticity = Config.turtleCollisionElasticity;
            return false;
        } else {
            return true; // eliminated
        }
    }

    public void makeString(ArrayRealVector velocity) {
        world.getWebs().add(new Web(this, velocity));
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public BufferedImage getImage() {
        BufferedImage image;
        if (intact) {
            image = wholeImage;
        } else {
            image = bodyImage;
        }
        AffineTransform tx = AffineTransform.getRotateInstance(rotation, (double) image.getWidth() / 2,
                (double) image.getHeight() / 2);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        return (op.filter(image, null));
    }
}
