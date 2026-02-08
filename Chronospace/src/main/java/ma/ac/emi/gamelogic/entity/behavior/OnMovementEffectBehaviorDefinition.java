package ma.ac.emi.gamelogic.entity.behavior;

public class OnMovementEffectBehaviorDefinition extends EntityBehaviorDefinition{
	private final String particleId;
	private final double emitterRadius;

    public OnMovementEffectBehaviorDefinition(String particleId, double emitterRadius) {
        this.particleId = particleId;
        this.emitterRadius = emitterRadius;
    }

    @Override
    public EntityBehavior create() {
        return new OnMovementEffectBehavior(particleId, emitterRadius);
    }
}
