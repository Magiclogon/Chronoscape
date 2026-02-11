package ma.ac.emi.gamelogic.weapon.behavior;

import java.util.ArrayList;
import java.util.List;

import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.particle.ParticleEmitter;
import ma.ac.emi.gamelogic.particle.lifecycle.AgeStrategy;
import ma.ac.emi.gamelogic.particle.lifecycle.OneTimeStrategy;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.math.Vector3D;

public class OnAttackEffectBehavior extends WeaponBehavior {
	private String particleId;
	private int count;
	private double radius;
	private double emitterRadius;
	private double ageMax;
	private boolean isOneTime;
	private boolean aligned;

	protected List<ParticleEmitter> emitters = new ArrayList<>();
	
	
	public OnAttackEffectBehavior(String particleId, double offsetX, double offsetY, int count, 
			double radius, double emitterRadius,
			double ageMax, boolean isOneTime, boolean aligned) {
		super(offsetX, offsetY);
		
		this.particleId = particleId;
		this.count = count;
		this.radius = radius;
		this.emitterRadius = emitterRadius;
		this.ageMax = ageMax;
		this.isOneTime = isOneTime;
		this.aligned = aligned;
	}

	
	@Override
	public void onInit(Weapon weapon) {
		emitters.clear();
		for(int i = 0; i < count; i++) {
			Vector3D offset = count == 1? new Vector3D() : Vector3D.randomUnit2().mult(Math.random() * radius);
			Vector3D dir = Vector3D.randomUnit2();
			ParticleEmitter emitter = initEmitter(offset, dir);
			if(isOneTime) emitter.setStrategy(new OneTimeStrategy());
			else emitter.setStrategy(new AgeStrategy());
			emitters.add(emitter);
		}
	}

	@Override
	public void onUpdate(Weapon weapon, double step) {		
	}
	
	@Override
	public void onAttack(Weapon weapon, double step) {
		//Spawn particles
		int invert = (int) Math.signum(weapon.getDir().dotP(new Vector3D(1, 0)));
		Vector3D relativeOffset = new Vector3D(offsetX, offsetY * invert);
		Vector3D offset = relativeOffset.rotateXY(weapon.getDir().getAngle());
		
		emitters.forEach(e -> {
			e.setPos(e.getPos()
					.add(weapon.getPos())
					.add(offset)
					.add(weapon.getBearer().getVelocity().mult(step)));
			
			if(aligned) e.setDir(weapon.getDir());
			e.setShouldEmit(true);
		});
		onInit(weapon);
	}
	
	private ParticleEmitter initEmitter(Vector3D pos, Vector3D dir) {
		return new ParticleEmitter(particleId, pos, dir, ageMax, emitterRadius, false);
	}

}
