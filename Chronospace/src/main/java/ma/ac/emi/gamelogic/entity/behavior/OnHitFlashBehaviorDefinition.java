package ma.ac.emi.gamelogic.entity.behavior;

public class OnHitFlashBehaviorDefinition extends EntityBehaviorDefinition{
	private double duration;
	private double intensity;
	
	public OnHitFlashBehaviorDefinition(double duration, double intensity) {
		this.duration = duration;
		this.intensity = intensity;
	}
	@Override
	public EntityBehavior create() {
		return new OnHitFlashBehavior(duration, intensity);
	}

}
