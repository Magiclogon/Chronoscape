package ma.ac.emi.gamelogic.attack.behavior.definition;

import ma.ac.emi.gamelogic.attack.behavior.ProjectileBehavior;
import ma.ac.emi.gamelogic.attack.behavior.ParticleTrailBehavior;

public class ParticleTrailBehaviorDefinition extends BehaviorDefinition {

	private final String particleId;
	private final double emitterRadius;

    public ParticleTrailBehaviorDefinition(String particleId, double emitterRadius) {
        this.particleId = particleId;
        this.emitterRadius = emitterRadius;
    }

    @Override
    public ProjectileBehavior create() {
        return new ParticleTrailBehavior(particleId, emitterRadius);
    }

}
