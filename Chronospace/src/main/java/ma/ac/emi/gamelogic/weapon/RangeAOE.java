package ma.ac.emi.gamelogic.weapon;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.attack.ProjectileAOE;
import ma.ac.emi.gamelogic.attack.manager.AttackObjectManager;
import ma.ac.emi.gamelogic.attack.type.ProjectileType;

@Getter
@Setter
public abstract class RangeAOE extends Weapon {
	protected ProjectileType projectileType;

	public RangeAOE() {
	
	}
	
	@Override
	public void attack() {
		if(getTsla() >= 1/getAttackSpeed()) {
			ProjectileAOE shotted = new ProjectileAOE(
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