package ma.ac.emi.gamelogic.weapon;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.projectile.ProjectileManager;

@Getter
@Setter
public abstract class RangeSingleHit extends Weapon {
	protected ProjectileManager projectileManager;

	public RangeSingleHit() {
		setAoe(0);
	}

}