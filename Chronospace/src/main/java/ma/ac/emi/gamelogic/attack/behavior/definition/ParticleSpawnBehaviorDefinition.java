package ma.ac.emi.gamelogic.attack.behavior.definition;

import ma.ac.emi.gamelogic.attack.behavior.Behavior;
import ma.ac.emi.gamelogic.attack.behavior.ParticleSpawnBehavior;

public class ParticleSpawnBehaviorDefinition extends BehaviorDefinition{

	private final String particleId;
    private final int count;
    private final double radius;
    private final double ageMax;
    private final boolean isOneTime;

    public ParticleSpawnBehaviorDefinition(String particleId, int count, double radius, double ageMax, boolean isOneTime) {
        this.particleId = particleId;
        this.count = count;
        this.radius = radius;
        this.ageMax = ageMax;
        this.isOneTime = isOneTime;
    }

    @Override
    public Behavior create() {
        return new ParticleSpawnBehavior(particleId, count, radius, ageMax, isOneTime);
    }

}
