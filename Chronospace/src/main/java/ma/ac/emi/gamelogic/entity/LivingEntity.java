package ma.ac.emi.gamelogic.entity;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.math.Vector2D;

@Setter
@Getter
public abstract class LivingEntity extends Entity {
    protected double hp;
    protected double hpMax;
    protected double strength;
    protected double regenerationSpeed;
    protected double speed;
    protected Weapon weapon;

    public LivingEntity(Vector2D pos, double speed) {
        super(pos);
        this.speed = speed;
    }
    
    public void attack() {
    	this.weapon.attack();
    }

}