package ma.ac.emi.gamelogic.attack.behavior.definition;

import ma.ac.emi.gamelogic.attack.behavior.Behavior;
import ma.ac.emi.gamelogic.attack.behavior.ParticleTrailBehavior;

public class ParticleTrailBehaviorDefinition extends BehaviorDefinition {

	private final String particleId;
	private final double emitterRadius;

    public ParticleTrailBehaviorDefinition(String particleId, double emitterRadius) {
        this.particleId = particleId;
        this.emitterRadius = emitterRadius;
    }

    @Override
    public Behavior create() {
        return new ParticleTrailBehavior(particleId, emitterRadius);
    }

}
