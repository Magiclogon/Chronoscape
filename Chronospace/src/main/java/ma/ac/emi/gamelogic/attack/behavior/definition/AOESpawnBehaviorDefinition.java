package ma.ac.emi.gamelogic.attack.behavior.definition;

import ma.ac.emi.gamelogic.attack.behavior.AOESpawnBehavior;
import ma.ac.emi.gamelogic.attack.behavior.ProjectileBehavior;

public class AOESpawnBehaviorDefinition extends BehaviorDefinition {

	private final String aoeId;
    private final int count;
    private final double radius;

    public AOESpawnBehaviorDefinition(String aoeId, int count, double radius) {
        this.aoeId = aoeId;
        this.count = count;
        this.radius = radius;
    }

    @Override
    public ProjectileBehavior create() {
        return new AOESpawnBehavior(aoeId, count, radius);
    }
}
