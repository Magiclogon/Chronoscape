package ma.ac.emi.gamelogic.particle.lifecycle;

import ma.ac.emi.gamelogic.particle.ParticleEmitter;

public class OneTimeStrategy implements EmitterLifeCycleStrategy{

	@Override
	public boolean shouldDesactivate(ParticleEmitter pe) {
		return pe.isHasEmitted();
	}

}
