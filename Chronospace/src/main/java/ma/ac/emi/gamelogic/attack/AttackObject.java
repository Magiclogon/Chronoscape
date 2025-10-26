package ma.ac.emi.gamelogic.attack;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.entity.Entity;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.math.Vector2D;
import ma.ac.emi.world.World;

@Getter
@Setter
public abstract class AttackObject extends Entity{
	private boolean active;    
    private Weapon weapon;
    private boolean fromPlayer;
    
   	public AttackObject(Vector2D pos, Weapon weapon) {
		super(pos);
		this.active = true;
		this.weapon = weapon;
		fromPlayer = weapon.isFromPlayer();
	}
   	
    public boolean isOutOfWorld(World world) {
    	return !(world.getBound().contains(this.getBound()));
    }
    
    public abstract void applyEffect(LivingEntity entity);
    
    public abstract void onDesactivate();
    
}
