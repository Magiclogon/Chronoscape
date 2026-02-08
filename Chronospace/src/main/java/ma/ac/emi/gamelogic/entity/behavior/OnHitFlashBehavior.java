package ma.ac.emi.gamelogic.entity.behavior;

import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.glgraphics.color.FlashEffect;

public class OnHitFlashBehavior implements EntityBehavior{
	private double intensity;
	private double duration;
	
	public OnHitFlashBehavior(double duration, double intensity) {
		this.intensity = intensity;
		this.duration = duration;
	}

	@Override
	public void onInit(LivingEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdate(LivingEntity entity, double step) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onHit(LivingEntity entity) {
		entity.addTemporaryEffect(new FlashEffect((float)duration, (float)intensity));
		
	}

	@Override
	public void onDeath(LivingEntity entity) {
		// TODO Auto-generated method stub
		
	}

}
