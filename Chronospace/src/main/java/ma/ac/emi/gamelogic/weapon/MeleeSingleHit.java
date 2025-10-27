package ma.ac.emi.gamelogic.weapon;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.attack.ProjectileSingleHit;
import ma.ac.emi.gamelogic.attack.type.ProjectileFactory;
import ma.ac.emi.gamelogic.attack.type.ProjectileType;

@Getter
@Setter
public abstract class MeleeSingleHit extends Weapon{
	private ProjectileType projectileType;
	
	public MeleeSingleHit() {
		projectileType = ProjectileFactory.getProjectileType(toString(), null, 500, 4, 4);
	}
	
	@Override
	public void attack() {
		if(getTsla() >= 1/getAttackSpeed()) {
			getAttackObjectManager().addObject(new ProjectileSingleHit(getPos(), getDir(), getProjectileType(), this));
			setTsla(0);
		}
	}
	
}
