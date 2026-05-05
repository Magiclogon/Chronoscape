package ma.ac.emi.gamelogic.difficulty;


public class MediumDifficultyStrategy extends AbstractDifficultyStrategy {

	@Override
	public double getEnemyHpScalingFactor() {return 0.01;}

	@Override
	public double getEnemyDamageScalingFactor() {return 0.08;}
	
	@Override
	public double getWaveTimerMultiplier() {return 1;}
}
