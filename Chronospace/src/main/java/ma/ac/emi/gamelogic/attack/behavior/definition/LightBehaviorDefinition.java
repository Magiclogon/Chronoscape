package ma.ac.emi.gamelogic.attack.behavior.definition;

import ma.ac.emi.gamelogic.attack.behavior.Behavior;
import ma.ac.emi.gamelogic.attack.behavior.LightBehavior;

public class LightBehaviorDefinition extends BehaviorDefinition{
	private final double lightRadius;
	private final double intensity;
	
	public LightBehaviorDefinition(double lightRadius, double intensity) {
		this.lightRadius = lightRadius;
		this.intensity = intensity;
	}
	
	
	@Override
	public Behavior create() {
		return new LightBehavior(lightRadius, intensity);
	}

}
