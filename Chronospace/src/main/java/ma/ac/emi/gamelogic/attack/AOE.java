package ma.ac.emi.gamelogic.attack;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.attack.type.AOEDefinition;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.math.Vector3D;

@Getter
@Setter
public class AOE extends AttackObject{
	private AOEDefinition aoeType;
	private double currentAge, lastAge;
	
	public AOE(Vector3D pos, AOEDefinition aoeType, Weapon weapon) {
		super(pos, weapon);
		
		this.aoeType = aoeType;
    	
        this.bound = new Rectangle(aoeType.getBoundWidth(), aoeType.getBoundHeight());
        currentAge = 0;
        lastAge = 0;
	}

	@Override
	public void update(double step) {
		bound.x = (int)getPos().getX();
		bound.y = (int)getPos().getY();
		currentAge += step;
		lastAge = currentAge - step;
		
		if(currentAge >= getAoeType().getAgeMax()) {
			setActive(false);
		}
	}

	@Override
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.cyan);
		g2d.fillRect((int)getPos().getX(), (int)getPos().getY(), bound.width, bound.height);
	}

	@Override
	public void applyEffect(LivingEntity entity) {
		double coolDown = 1/getAoeType().getEffectRate();
		double closestInfCoolDownMultiple = Math.floor(getCurrentAge()/coolDown) * coolDown;
		
		if(getLastAge() <= closestInfCoolDownMultiple && getCurrentAge() >= closestInfCoolDownMultiple) {
	    	WeaponItemDefinition definition = (WeaponItemDefinition) getWeapon().getWeaponItem().getItemDefinition();
			entity.setHp(Math.max(0, entity.getHp() - definition.getDamage()));
		}
		
	}

}
