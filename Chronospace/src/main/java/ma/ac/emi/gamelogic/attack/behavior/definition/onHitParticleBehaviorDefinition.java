package ma.ac.emi.gamelogic.attack.behavior.definition;

import lombok.Getter;
import ma.ac.emi.gamelogic.attack.behavior.Behavior;
import ma.ac.emi.gamelogic.attack.behavior.ParticleSpawnBehavior;
import ma.ac.emi.gamelogic.attack.behavior.onHitParticleBehavior;
import ma.ac.emi.gamelogic.particle.ParticleEmitter;

@Getter
public class onHitParticleBehaviorDefinition extends BehaviorDefinition{

	protected String enemyParticleId;
	protected String ObstacleParticleId;
	protected double emitterRadius;
	

	public onHitParticleBehaviorDefinition(String enemyParticleId, String ObstacleParticleId, double emitterRadius) {
		this.enemyParticleId = enemyParticleId;
		this.ObstacleParticleId = ObstacleParticleId;
	}
		

    @Override
    public Behavior create() {
        return new onHitParticleBehavior(enemyParticleId, ObstacleParticleId, emitterRadius);
    }

}
