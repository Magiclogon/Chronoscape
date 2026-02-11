package ma.ac.emi.gamelogic.entity.behavior;

import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.particle.ParticleEmitter;
import ma.ac.emi.gamelogic.particle.lifecycle.UndeterminedStrategy;
import ma.ac.emi.gamelogic.player.Player;

public class OnMovementEffectBehavior implements EntityBehavior{
	private String particleId;
	private ParticleEmitter emitter;
	private double emitterRadius;
	
	public OnMovementEffectBehavior(String particleId, double emitterRadius) {
		this.particleId = particleId;
		this.emitterRadius = emitterRadius;
	}
	
	@Override
	public void onInit(LivingEntity entity) {
		this.emitter = new ParticleEmitter(particleId, entity.getPos(), entity.getVelocity(), 999, emitterRadius);
		this.emitter.setStrategy(new UndeterminedStrategy());
	}

	@Override
	public void onUpdate(LivingEntity entity, double step) {
		if(entity.getVelocity() != null)
			if(entity.getVelocity().norm() != 0) 
				emitter.setShouldEmit(true);
			else emitter.setShouldEmit(false);
		else emitter.setShouldEmit(false);
		
		emitter.setPos(entity.getPos());
		emitter.setDir(entity.getVelocity());
	}

	@Override
	public void onHit(LivingEntity entity) {}

	@Override
	public void onDeath(LivingEntity entity) {
		this.emitter.setActive(false);
	}

}
