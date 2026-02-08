package ma.ac.emi.gamelogic.entity.behavior;

public class OnDeathEffectBehaviorDefinition extends EntityBehaviorDefinition{
	private final String particleId;
    private final int count;
    private final double radius;
    private final double emitterRadius;
    private final double ageMax;
    private final boolean isOneTime;

    public OnDeathEffectBehaviorDefinition(String particleId, int count, double radius, double emitterRadius, double ageMax, boolean isOneTime) {
        this.particleId = particleId;
        this.count = count;
        this.radius = radius;
        this.emitterRadius = emitterRadius;
        this.ageMax = ageMax;
        this.isOneTime = isOneTime;
    }

    @Override
    public EntityBehavior create() {
        return new OnDeathEffectBehavior(particleId, count, radius, emitterRadius, ageMax, isOneTime);
    }
}