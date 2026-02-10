package ma.ac.emi.gamelogic.weapon.behavior;

public class OnAttackEffectBehaviorDefinition extends WeaponBehaviorDefinition {
	private String particleId;
	private int count;
	private double radius;
	private double emitterRadius;
	private double ageMax;
	private boolean isOneTime;
	
	
	public OnAttackEffectBehaviorDefinition(String particleId, double offset, int count, double radius, double emitterRadius,
			double ageMax, boolean isOneTime) {
		super(offset);
		
		this.particleId = particleId;
		this.offset = offset;
		this.count = count;
		this.radius = radius;
		this.emitterRadius = emitterRadius;
		this.ageMax = ageMax;
		this.isOneTime = isOneTime;
	}


	@Override
	public WeaponBehavior create() {
		return new OnAttackEffectBehavior(particleId, offset, count, radius, emitterRadius, ageMax, isOneTime);
	}

}
