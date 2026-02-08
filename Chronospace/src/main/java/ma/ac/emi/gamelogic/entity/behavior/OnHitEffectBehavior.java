package ma.ac.emi.gamelogic.entity.behavior;

import java.util.ArrayList;
import java.util.List;

import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.particle.ParticleEmitter;
import ma.ac.emi.gamelogic.particle.lifecycle.AgeStrategy;
import ma.ac.emi.gamelogic.particle.lifecycle.OneTimeStrategy;
import ma.ac.emi.math.Vector3D;

public class OnHitEffectBehavior implements EntityBehavior{
	protected String particleId;
	protected int count;
	protected double radius;
	protected double emitterRadius;
	protected double ageMax;
	protected boolean isOneTime;
	protected List<ParticleEmitter> emitters = new ArrayList<>();

	public OnHitEffectBehavior(String particleId, int count, double radius, double emitterRadius, double ageMax, boolean isOneTime) {
		this.particleId = particleId;
		this.count = count;
		this.radius = radius;
		this.ageMax = ageMax;
		this.isOneTime = isOneTime;
		this.emitterRadius = emitterRadius;
	}
	

	public void onInit(LivingEntity entity) {
		for(int i = 0; i < count; i++) {
			Vector3D offset = count == 1? new Vector3D() : Vector3D.randomUnit2().mult(Math.random() * radius);
			ParticleEmitter emitter = initEmitter(offset);
			if(isOneTime) emitter.setStrategy(new OneTimeStrategy());
			else emitter.setStrategy(new AgeStrategy());
			emitters.add(emitter);
		}
	}

	@Override
	public void onUpdate(LivingEntity entity, double step) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onHit(LivingEntity entity) {
		//Spawn particles
		if(entity.getHp() <= 0) return;
		emitters.forEach(e -> {
			e.setPos(e.getPos().add(entity.getPos()));
			e.setShouldEmit(true);
		});
		onInit(entity);
	}

	@Override
	public void onDeath(LivingEntity entity) {
		
	}
	
	private ParticleEmitter initEmitter(Vector3D pos) {
		return new ParticleEmitter(particleId, pos, ageMax, emitterRadius, false);
	}
}
