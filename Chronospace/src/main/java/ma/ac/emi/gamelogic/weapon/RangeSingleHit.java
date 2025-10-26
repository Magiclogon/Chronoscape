package ma.ac.emi.gamelogic.weapon;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.attack.ProjectileSingleHit;
import ma.ac.emi.gamelogic.attack.manager.AttackObjectManager;
import ma.ac.emi.gamelogic.attack.type.ProjectileType;

@Getter
@Setter
public abstract class RangeSingleHit extends Weapon {
	protected AttackObjectManager attackObjectManager;
	protected ProjectileType projectileType;

	public RangeSingleHit() {
		setAoe(0);
	}
	
	public void attack() {
		if(getTsla() >= 1/getAttackSpeed()) {
			ProjectileSingleHit shotted = new ProjectileSingleHit(
					getPos(), 
					getDir(),
					getProjectileType(),
					this);
			attackObjectManager.addObject(shotted);
			setTsla(0);
		}
	}

	@Override
	public void update(double step) {
		super.update(step);
	}


}