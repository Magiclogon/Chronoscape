package ma.ac.emi.gamelogic.attack.behavior.definition;

import ma.ac.emi.gamelogic.attack.behavior.Behavior;
import ma.ac.emi.gamelogic.attack.behavior.LightBehavior;
import ma.ac.emi.glgraphics.lighting.LightObject;

public class LightBehaviorDefinition extends BehaviorDefinition{
	private final double lightRadius;
	private double r, g, b;
	private double intensity;
	
	private LightObject light;
	
	public LightBehaviorDefinition(double lightRadius, double r, double g, double b, double intensity) {
		this.lightRadius = lightRadius;
		this.r = r; this.g = g; this.b = b;
		this.intensity = intensity;
	}
	
	
	@Override
	public Behavior create() {
		return new LightBehavior(lightRadius, r, g, b, intensity);
	}

}
