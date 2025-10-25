package ma.ac.emi.gamelogic.weapon;

import java.awt.Rectangle;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.projectile.Projectile;
import ma.ac.emi.gamelogic.projectile.ProjectileManager;

@Getter
@Setter
public abstract class RangeSingleHit extends Weapon {
	protected double projectileSpeed;
	protected ProjectileManager projectileManager;
	protected Projectile example;

	public RangeSingleHit() {
		setAoe(0);
	}
	
	@Override
	public void attack() {
		if(getTsla() >= 1/getAttackSpeed()) {
			Projectile shotted = new Projectile(getExample());
			shotted.setPos(getPos());
			shotted.setVelocity(getDir().mult(projectileSpeed));
			shotted.setFromPlayer(getBearer() instanceof Player);
			projectileManager.addProjectile(shotted);
			setTsla(0);
		}
	}

	@Override
	public void update(double step) {
		super.update(step);
	}


}