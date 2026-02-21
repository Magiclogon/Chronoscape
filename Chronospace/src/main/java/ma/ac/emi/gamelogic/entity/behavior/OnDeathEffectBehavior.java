package ma.ac.emi.gamelogic.entity.behavior;

import java.util.ArrayList;
import java.util.List;

import ma.ac.emi.gamelogic.particle.ParticleEmitter;
import ma.ac.emi.gamelogic.particle.lifecycle.AgeStrategy;
import ma.ac.emi.gamelogic.particle.lifecycle.OneTimeStrategy;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.math.Vector3D;

public class OnDeathEffectBehavior implements EntityBehavior{
	private String particleId;
	private int count;
	private double radius;
	private double emitterRadius;
	private double ageMax;
	private boolean isOneTime;

	protected List<ParticleEmitter> emitters = new ArrayList<>();
	protected List<Vector3D> randomOffsets = new ArrayList<>();
	
	public OnDeathEffectBehavior(String particleId, int count, 
			double radius, double emitterRadius,
			double ageMax, boolean isOneTime) {
		
		this.particleId = particleId;
		this.count = count;
		this.radius = radius;
		this.emitterRadius = emitterRadius;
		this.ageMax = ageMax;
		this.isOneTime = isOneTime;
	}

	
	@Override
	public void onInit(LivingEntity entity) {
		emitters.clear();
		randomOffsets.clear();
		for(int i = 0; i < count; i++) {
			Vector3D offset = count == 1? new Vector3D() : Vector3D.randomUnit2().mult(Math.random() * radius);
			Vector3D dir = Vector3D.randomUnit2();
			ParticleEmitter emitter = initEmitter(offset, dir);
			if(isOneTime) emitter.setStrategy(new OneTimeStrategy());
			else emitter.setStrategy(new AgeStrategy());
			emitters.add(emitter);
			randomOffsets.add(offset);
		}
	}
	


	@Override
	public void onSpawn(LivingEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdate(LivingEntity entity, double step) {		
		if(entity.getDir() == null) return;
		
		
		for(int i = 0; i < emitters.size(); i++) {
			ParticleEmitter emitter = emitters.get(i);
			Vector3D randomOffset = randomOffsets.get(i);
			
			emitter.setPos(entity.getPos()
					.add(randomOffset));
			
			if(!emitter.isActive()) emitter.activate();
		}
	}
	
	@Override
	public void onHit(LivingEntity entity) {
		
	}

	@Override
	public void onDeath(LivingEntity entity) {
		//Spawn particles
		emitters.forEach(e -> {
			e.setShouldEmit(true);
		});
		
	}
	
	private ParticleEmitter initEmitter(Vector3D pos, Vector3D dir) {
		return new ParticleEmitter(particleId, pos, dir, ageMax, emitterRadius, false);
	}

	
}
