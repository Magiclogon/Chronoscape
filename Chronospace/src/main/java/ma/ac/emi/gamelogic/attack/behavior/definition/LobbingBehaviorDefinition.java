package ma.ac.emi.gamelogic.attack.behavior.definition;

import ma.ac.emi.gamelogic.attack.behavior.Behavior;
import ma.ac.emi.gamelogic.attack.behavior.LobbingBehavior;

public class LobbingBehaviorDefinition extends BehaviorDefinition{
	private double g;
	private double scale;
	
	public LobbingBehaviorDefinition(double g, double scale) {
		this.g = g;
		this.scale = scale;
	}
	
	@Override
	public Behavior create() {
		return new LobbingBehavior(g, scale);
	}

}
