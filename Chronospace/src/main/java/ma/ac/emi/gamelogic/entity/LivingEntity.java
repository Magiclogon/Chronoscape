package ma.ac.emi.gamelogic.entity;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.attack.manager.AttackObjectManager;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.math.Vector3D;

@Setter
@Getter
public abstract class LivingEntity extends Entity {
    public LivingEntity() {
		super(true);
	}

	protected double hp;
    protected double hpMax;
    protected double strength;
    protected double regenerationSpeed;
    protected double speed;
    
    protected AttackObjectManager attackObjectManager;

    public abstract void attack();

}