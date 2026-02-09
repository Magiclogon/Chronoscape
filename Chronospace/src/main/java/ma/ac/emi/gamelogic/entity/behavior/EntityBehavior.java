package ma.ac.emi.gamelogic.entity.behavior;

import ma.ac.emi.gamelogic.entity.LivingEntity;

public interface EntityBehavior {
	void onInit(LivingEntity entity);
	void onUpdate(LivingEntity entity, double step);
	void onHit(LivingEntity entity);
	void onDeath(LivingEntity entity);
}
