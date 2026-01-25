package ma.ac.emi.gamelogic.attack.behavior.definition;

import ma.ac.emi.gamelogic.attack.behavior.Behavior;
import ma.ac.emi.gamelogic.attack.behavior.ParticleTrailBehavior;

public class ParticleTrailBehaviorDefinition extends BehaviorDefinition {

	private final String particleId;

    public ParticleTrailBehaviorDefinition(String particleId) {
        this.particleId = particleId;
    }

    @Override
    public Behavior create() {
        return new ParticleTrailBehavior(particleId);
    }

}
