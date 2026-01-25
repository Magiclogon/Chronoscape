package ma.ac.emi.gamelogic.particle;

import java.awt.Graphics;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamecontrol.GameObject;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamecontrol.GameTime;
import ma.ac.emi.gamelogic.particle.lifecycle.EmitterLifeCycleStrategy;
import ma.ac.emi.gamelogic.particle.lifecycle.OneTimeStrategy;
import ma.ac.emi.math.Vector3D;

@Getter
@Setter
public class ParticleEmitter extends GameObject{
	private String particleId;
	private double age;

	private boolean shouldEmit;
	private boolean isActive;
	private boolean hasEmitted;
	
	private EmitterLifeCycleStrategy strategy;
	
	public ParticleEmitter(String particleId, Vector3D pos, double ageMax) {
		this(particleId, pos, ageMax, true);
	}
	
	public ParticleEmitter(String particleId, Vector3D pos, double ageMax, boolean shouldEmit) {
		this.particleId = particleId;
		this.pos = pos;
		this.drawn = false;
		this.isActive = true;
		this.shouldEmit = shouldEmit;
		this.hasEmitted = false;
		this.age = ageMax;
		
		GameController.getInstance().getParticleSystem().getEmitterManager().addEmitter(this);
	}

	@Override
	public void draw(Graphics g) {		
	}

	@Override
	public void update(double step) {
		Vector3D offset = Vector3D.randomUnit2().mult(Math.random() * 5);
		Vector3D spawnPos = pos.add(offset);
		
		if(!GameController.getInstance().getWorldManager().getCurrentWorld().isObstacle(
				(int)((spawnPos.getX()+GamePanel.TILE_SIZE/2)/GamePanel.TILE_SIZE), 
				(int)((spawnPos.getY()+GamePanel.TILE_SIZE/2)/GamePanel.TILE_SIZE))
			|| strategy instanceof OneTimeStrategy) 
		{
			GameController.getInstance().getParticleSystem().spawnEffect(particleId, spawnPos, this, GameTime.get());
			hasEmitted = true;
		}
		if(strategy.shouldDesactivate(this))
			isActive = false;
		
		age -= step;
	}

}
