package ma.ac.emi.gamelogic.attack.behavior.definition;

import lombok.Getter;
import ma.ac.emi.gamelogic.attack.behavior.Behavior;
import ma.ac.emi.gamelogic.attack.behavior.LightSpawnBehavior;

@Getter
public class LightSpawnBehaviorDefinition extends BehaviorDefinition{

	private double lightRadius;
	private double ageMax;
	private int count;
	private double radius;
	private double intensity;

	public LightSpawnBehaviorDefinition(int count, double radius, double lightRadius, double ageMax, double intensity) {
		this.count = count;
		this.radius = radius;
		this.lightRadius = lightRadius;
		this.ageMax = ageMax;
		this.intensity = intensity;
	}
	
	@Override
    public Behavior create() {
        return new LightSpawnBehavior(getCount(), getRadius(), getLightRadius(), getAgeMax(), getIntensity());
    }

}
