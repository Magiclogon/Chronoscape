package ma.ac.emi.gamelogic.attack.behavior;

import ma.ac.emi.gamelogic.attack.Projectile;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.particle.ParticleEmitter;
import ma.ac.emi.gamelogic.particle.lifecycle.UndeterminedStrategy;

public class ParticleTrailBehavior implements Behavior{
	private String particleId;
	private ParticleEmitter emitter;
	private double emitterRadius;
	
	public ParticleTrailBehavior(String particleId, double emitterRadius) {
		this.particleId = particleId;
		this.emitterRadius = emitterRadius;
	}
	
	@Override
	public void onInit(Projectile p) {
		this.emitter = new ParticleEmitter(particleId, p.getPos(), p.getVelocity(), 999, emitterRadius);
		this.emitter.setStrategy(new UndeterminedStrategy());
	}

	@Override
	public void onUpdate(Projectile p, double step) {
		//Spawn particles
		emitter.setPos(p.getPos());
		emitter.setDir(p.getVelocity());
	}

	@Override
	public void onHit(Projectile p, LivingEntity entity) {}

	@Override
	public void onDesactivate(Projectile p) {
		this.emitter.setActive(false);
	}

}
