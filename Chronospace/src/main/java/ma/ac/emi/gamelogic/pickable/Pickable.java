package ma.ac.emi.gamelogic.pickable;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.entity.Entity;
import ma.ac.emi.gamelogic.physics.AABB;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.math.Vector3D;

import java.awt.*;

@Getter
@Setter
public abstract class Pickable extends Entity {
    protected double dropProbability;
    protected boolean isPickedUp;

    // Generic value (can be money, ...)
    protected double value;

    public Pickable(double dropProbability, double value) {
        this.dropProbability = dropProbability;
        this.value = value;
        this.isPickedUp = false;
        this.hitbox = new AABB(new Vector3D(), new Vector3D(10, 10));
    }

    public abstract void applyEffect(Player player);

    // To apply difficulty (multiplier)
    public void applyValueMultiplier(double multiplier) {
        this.value *= multiplier;
    }

    public abstract Pickable createInstance();

    @Override
    public void update(double step) {
        hitbox.center = new Vector3D(pos.getX(), pos.getY());
        if(isPickedUp) {
        	GameController.getInstance().removeDrawable(this);
        }
    }

    @Override
    public void initStateMachine() {
        // TODO
    }

    @Override
    public void setupAnimations() {
        // TODO
    }
}