package ma.ac.emi.gamelogic.attack.behavior;

import ma.ac.emi.gamelogic.attack.Projectile;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.particle.ParticleEmitter;
import ma.ac.emi.gamelogic.particle.lifecycle.UndeterminedStrategy;

public class ParticleTrailBehavior implements Behavior{
	private String particleId;
	private ParticleEmitter emitter;
	
	public ParticleTrailBehavior(String particleId) {
		this.particleId = particleId;
	}
	
	@Override
	public void onInit(Projectile p) {
		this.emitter = new ParticleEmitter(particleId, p.getPos(), 999);
		this.emitter.setStrategy(new UndeterminedStrategy());
	}

	@Override
	public void onUpdate(Projectile p, double step) {
		//Spawn particles
		emitter.setPos(p.getPos());
	}

	@Override
	public void onHit(Projectile p, LivingEntity entity) {}

	@Override
	public void onDesactivate(Projectile p) {
		this.emitter.setActive(false);
	}

}
