package ma.ac.emi.gamelogic.attack.behavior.definition;

import ma.ac.emi.gamelogic.attack.behavior.Behavior;
import ma.ac.emi.gamelogic.attack.behavior.DamageBehavior;

public class DamageBehaviorDefinition extends BehaviorDefinition {

	private final boolean destroyOnHit;

    public DamageBehaviorDefinition(boolean destroyOnHit) {
        this.destroyOnHit = destroyOnHit;
    }

    @Override
    public Behavior create() {
        return new DamageBehavior(destroyOnHit);
    }
}
