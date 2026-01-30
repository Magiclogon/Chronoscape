package ma.ac.emi.gamelogic.particle.lifecycle;

import ma.ac.emi.gamelogic.particle.ParticleEmitter;

public class UndeterminedStrategy implements EmitterLifeCycleStrategy{

	@Override
	public boolean shouldDesactivate(ParticleEmitter pe) {
		return false;
	}

}
