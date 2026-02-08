package ma.ac.emi.gamelogic.entity.behavior;

public class OnHitInvincibilityBehaviorDefinition extends EntityBehaviorDefinition{
	private double duration;
	private double flashingFrequency;
	
	
	public OnHitInvincibilityBehaviorDefinition(double duration, double flashinFrequency) {
		this.duration = duration;
		this.flashingFrequency = flashinFrequency;
	}

	@Override
	public EntityBehavior create() {
		return new OnHitInvincibilityBehavior(duration, flashingFrequency);
	}

}
