package ma.ac.emi.gamelogic.attack.behavior;

import java.util.ArrayList;
import java.util.List;

import ma.ac.emi.gamelogic.attack.Projectile;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.particle.ParticleEmitter;
import ma.ac.emi.gamelogic.particle.lifecycle.AgeStrategy;
import ma.ac.emi.gamelogic.particle.lifecycle.OneTimeStrategy;
import ma.ac.emi.math.Vector3D;
import ma.ac.emi.world.Obstacle;

public class onHitParticleBehavior implements Behavior{
	protected String enemyParticleId;
	protected String obstacleParticleId;
	protected double emitterRadius;
	
	protected ParticleEmitter emitter;

	public onHitParticleBehavior(String enemyParticleId, String obstacleParticleId, double emitterRadius) {
		this.enemyParticleId = enemyParticleId;
		this.obstacleParticleId = obstacleParticleId;
		this.emitterRadius = emitterRadius;
	}
	

	@Override
	public void onInit(Projectile p) {
		emitter = initEmitter(p.getPos(), p.getVelocity());
		emitter.setStrategy(new OneTimeStrategy());

	}

	@Override
	public void onUpdate(Projectile p, double step) {
	}

	@Override
	public void onHit(Projectile p, LivingEntity entity) {
		emitter.setParticleId(enemyParticleId);
		emitter.setPos((p.getPos()));
		emitter.setShouldEmit(true);
		
		onInit(p);
	}

	@Override
	public void onDesactivate(Projectile p) {}
	
	private ParticleEmitter initEmitter(Vector3D pos, Vector3D dir) {
		return new ParticleEmitter(enemyParticleId, pos, dir, 999, emitterRadius, false);
	}


	@Override
	public void onHit(Projectile p, Obstacle obstacle) {
		emitter.setParticleId(obstacleParticleId);
		emitter.setPos((p.getPos()));
		emitter.setShouldEmit(true);
		
		onInit(p);
	}

}
