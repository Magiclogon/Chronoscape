package ma.ac.emi.gamelogic.entity.behavior;

import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.glgraphics.color.InvincibilityFlashingEffect;

public class OnHitInvincibilityBehavior implements EntityBehavior{
	private double duration;
	private double flashingFrequency;
	
	private double timeTracker;
	
	public OnHitInvincibilityBehavior(double duration, double flashinFrequency) {
		this.duration = duration;
		this.flashingFrequency = flashinFrequency;
	}

	@Override
	public void onInit(LivingEntity entity) {
		timeTracker = 0;
	}

	@Override
	public void onUpdate(LivingEntity entity, double step) {
		timeTracker -= step;
		if(timeTracker > 0) {
			entity.setInvincible(true);
		}
		
	}

	@Override
	public void onHit(LivingEntity entity) {
		if(entity.getHp() <= 0) return;
		timeTracker = duration;
		entity.addTemporaryEffect(new InvincibilityFlashingEffect(duration, flashingFrequency));
	}

	@Override
	public void onDeath(LivingEntity entity) {
		// TODO Auto-generated method stub
		
	}

}
