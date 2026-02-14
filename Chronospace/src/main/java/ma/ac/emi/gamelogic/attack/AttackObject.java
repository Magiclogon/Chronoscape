package ma.ac.emi.gamelogic.attack;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.fx.AnimationState;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.entity.Entity;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.math.Vector3D;
import ma.ac.emi.world.World;
import ma.ac.emi.world.WorldContext;

@Getter
@Setter
public abstract class AttackObject extends Entity{
	protected boolean active;    
    protected Weapon weapon;
    protected boolean fromPlayer;
    
   	public AttackObject() {
		this.active = false;
    	GameController.getInstance().removeDrawable(this);

	}
   	
   	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
		fromPlayer = weapon.isFromPlayer();
   	}
   	
   	public void initStateMachine() {}
   	
   	public void setupAnimations() {}
   	
    public boolean isOutOfWorld(WorldContext context) {
    	return !(context.getWorldBounds().intersects(this.getHitbox()));
    }
    
    public abstract void applyEffect(LivingEntity entity);
    
    public abstract void onDesactivate();
    
	public void desactivate() {
		getPos().setZ(0);
    	setActive(false);
	}
	public void activate() {
		setActive(true);
	}
    
}
