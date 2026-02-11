package ma.ac.emi.gamelogic.weapon.behavior;

public class OnAttackEffectBehaviorDefinition extends WeaponBehaviorDefinition {
	private String particleId;
	private int count;
	private double radius;
	private double emitterRadius;
	private double ageMax;
	private boolean isOneTime;
	private boolean aligned;
	
	
	public OnAttackEffectBehaviorDefinition(String particleId, double offsetX, double offsetY, 
			int count, double radius, double emitterRadius,
			double ageMax, boolean isOneTime, boolean aligned) {
		super(offsetX, offsetY);
		
		this.particleId = particleId;
		this.count = count;
		this.radius = radius;
		this.emitterRadius = emitterRadius;
		this.ageMax = ageMax;
		this.isOneTime = isOneTime;
		this.aligned = aligned;
	}


	@Override
	public WeaponBehavior create() {
		return new OnAttackEffectBehavior(particleId, offsetX, offsetY, count, radius, emitterRadius, ageMax, isOneTime, aligned);
	}

}
