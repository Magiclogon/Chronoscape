package ma.ac.emi.gamelogic.attack.behavior;

import java.util.ArrayList;
import java.util.List;

import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamecontrol.GameTime;
import ma.ac.emi.gamelogic.attack.Projectile;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.particle.ParticleEmitter;
import ma.ac.emi.gamelogic.particle.lifecycle.AgeStrategy;
import ma.ac.emi.gamelogic.particle.lifecycle.OneTimeStrategy;
import ma.ac.emi.math.Vector3D;

public class ParticleSpawnBehavior implements Behavior{
	private String particleId;
	private int count;
	private double radius;
	private double emitterRadius;
	private double ageMax;
	private boolean isOneTime;
	private List<ParticleEmitter> emitters = new ArrayList<>();

	public ParticleSpawnBehavior(String particleId, int count, double radius, double emitterRadius, double ageMax, boolean isOneTime) {
		this.particleId = particleId;
		this.count = count;
		this.radius = radius;
		this.ageMax = ageMax;
		this.isOneTime = isOneTime;
		this.emitterRadius = emitterRadius;
	}
	

	@Override
	public void onInit(Projectile p) {
		for(int i = 0; i < count; i++) {
			Vector3D offset = count == 1? new Vector3D() : Vector3D.randomUnit2().mult(Math.random() * radius);
			ParticleEmitter emitter = new ParticleEmitter(particleId, offset, ageMax, emitterRadius, false);
			if(isOneTime) emitter.setStrategy(new OneTimeStrategy());
			else emitter.setStrategy(new AgeStrategy());
			emitters.add(emitter);
		}
	}

	@Override
	public void onUpdate(Projectile p, double step) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onHit(Projectile p, LivingEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDesactivate(Projectile p) {
		//Spawn particles
		emitters.forEach(e -> {
			e.setPos(e.getPos().add(p.getPos()));
			e.setShouldEmit(true);
		});
		
	}


}
