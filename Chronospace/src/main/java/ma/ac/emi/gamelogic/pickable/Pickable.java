package ma.ac.emi.gamelogic.pickable;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.difficulty.DifficultyObserver;
import ma.ac.emi.gamelogic.entity.Entity;
import ma.ac.emi.gamelogic.physics.AABB;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.math.Vector3D;

import java.awt.*;

@Getter
@Setter
public abstract class Pickable extends Entity{
    protected double dropProbability;
    protected boolean isPickedUp;

    public Pickable(double dropProbability) {
        this.dropProbability = dropProbability;
        this.isPickedUp = false;
        this.hitbox = new AABB(new Vector3D(), new Vector3D(10, 10));
        
    }

    public abstract void applyEffect(Player player);

    // For difficulty
    public abstract void adjustForDifficulty(double difficultyMultiplier);

    // Create with base values
    public abstract Pickable createInstance();
    
    public void update(double step) {
        hitbox.center = new Vector3D(pos.getX(), pos.getY());
        if(isPickedUp) {
        	GameController.getInstance().removeDrawable(this);
        }
    }
    

	@Override
	public void initStateMachine() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setupAnimations() {
		// TODO Auto-generated method stub
		
	}
}
