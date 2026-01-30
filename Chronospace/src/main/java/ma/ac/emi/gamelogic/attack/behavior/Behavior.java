package ma.ac.emi.gamelogic.attack.behavior;

import ma.ac.emi.gamelogic.attack.Projectile;
import ma.ac.emi.gamelogic.entity.LivingEntity;

public interface Behavior {
	void onInit(Projectile p);
	void onUpdate(Projectile p, double step);
	void onHit(Projectile p, LivingEntity entity);
	void onDesactivate(Projectile p);
}
