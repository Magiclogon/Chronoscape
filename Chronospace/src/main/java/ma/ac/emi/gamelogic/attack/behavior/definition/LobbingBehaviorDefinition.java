package ma.ac.emi.gamelogic.attack.behavior.definition;

import ma.ac.emi.gamelogic.attack.behavior.Behavior;
import ma.ac.emi.gamelogic.attack.behavior.LobbingBehavior;

public class LobbingBehaviorDefinition extends BehaviorDefinition{
	private double g;
	
	public LobbingBehaviorDefinition(double g) {
		this.g = g;
	}
	
	@Override
	public Behavior create() {
		return new LobbingBehavior(g);
	}

}
