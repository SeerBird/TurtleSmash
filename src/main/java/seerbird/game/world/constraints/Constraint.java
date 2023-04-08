package seerbird.game.world.constraints;

public interface Constraint extends Cloneable {
    boolean satisfy();
    Constraint clone();
}
