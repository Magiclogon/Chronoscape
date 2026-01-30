package ma.ac.emi.gamelogic.particle.lifecycle;

import ma.ac.emi.gamelogic.particle.ParticleEmitter;

public interface EmitterLifeCycleStrategy {
	boolean shouldDesactivate(ParticleEmitter pe);
}
