package ma.ac.emi.gamelogic.particle.lifecycle;

import ma.ac.emi.gamelogic.particle.ParticleEmitter;

public class AgeStrategy implements EmitterLifeCycleStrategy{

	@Override
	public boolean shouldDesactivate(ParticleEmitter pe) {
		return pe.getAge() <= 0;
	}

}
