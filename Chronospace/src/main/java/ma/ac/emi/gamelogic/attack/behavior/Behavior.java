package ma.ac.emi.gamelogic.attack.behavior;

import ma.ac.emi.gamelogic.attack.Projectile;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.world.Obstacle;

public interface Behavior {
	void onInit(Projectile p);
	void onUpdate(Projectile p, double step);
	void onHit(Projectile p, LivingEntity entity);
	void onHit(Projectile p, Obstacle obstacle);
	void onDesactivate(Projectile p);
}
