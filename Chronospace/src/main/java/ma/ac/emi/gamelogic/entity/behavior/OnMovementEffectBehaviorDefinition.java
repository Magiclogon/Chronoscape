package ma.ac.emi.gamelogic.entity.behavior;

public class OnMovementEffectBehaviorDefinition extends EntityBehaviorDefinition{
	private final String particleId;
	private final double emitterRadius;
	private final double offsetX, offsetY;

    public OnMovementEffectBehaviorDefinition(String particleId, double emitterRadius, double offsetX, double offsetY) {
        this.particleId = particleId;
        this.emitterRadius = emitterRadius;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
    }

    @Override
    public EntityBehavior create() {
        return new OnMovementEffectBehavior(particleId, emitterRadius, offsetX, offsetY);
    }
}
