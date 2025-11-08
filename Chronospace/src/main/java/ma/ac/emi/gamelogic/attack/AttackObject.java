package ma.ac.emi.gamelogic.attack;

import lombok.Getter;
import lombok.Setter;
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
    
   	public AttackObject(Vector3D pos, Weapon weapon) {
   		super(true);
   		this.pos = pos;
		this.active = true;
		this.weapon = weapon;
		fromPlayer = weapon.isFromPlayer();
	}
   	
    public boolean isOutOfWorld(WorldContext context) {
    	return !(context.getWorldBounds().intersects(this.getBound()));
    }
    
    public abstract void applyEffect(LivingEntity entity);
    
    public void onDesactivate() {
		GameController.getInstance().getGamePanel().removeDrawable(this);

    }
    
}
