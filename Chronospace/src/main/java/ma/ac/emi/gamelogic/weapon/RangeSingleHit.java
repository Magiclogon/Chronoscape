package ma.ac.emi.gamelogic.weapon;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.attack.ProjectileSingleHit;
import ma.ac.emi.gamelogic.attack.type.ProjectileType;

@Getter
@Setter
public abstract class RangeSingleHit extends Weapon {
	protected ProjectileType projectileType;

	public RangeSingleHit() {
	}
	
	public void attack() {
		if(getTsla() >= 1/getAttackSpeed() && getAmmo() > 0) {
			ProjectileSingleHit shotted = new ProjectileSingleHit(
					getPos(), 
					getDir(),
					getProjectileType(),
					this);
			attackObjectManager.addObject(shotted);
			setAmmo(getAmmo() -1);
			setTsla(0);
		}
	}

	@Override
	public void update(double step) {
		super.update(step);
	}


}