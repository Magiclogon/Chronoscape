package ma.ac.emi.gamelogic.difficulty;

public class HardDifficultyStrategy extends AbstractDifficultyStrategy {
    @Override
    public double getEnemyHpMultiplier() { return 1.5; }

    @Override
    public double getEnemyDamageMultiplier() { return 2.0; }

    @Override
    public double getPickableDropRate() { return 0.3; }

    @Override
    public double getEnemyCountMultiplier() { return 1.5; }

    @Override
    public double getBossSpawnCountMultiplier() { return 1.5; }
    @Override
    public double getBossSpawnRateMultiplier() { return 1.5; }

	@Override
	public double getEnemyHpScalingFactor() {return 0.015;}

	@Override
	public double getEnemyDamageScalingFactor() {return 0.12;}
	
	@Override
	public double getWaveTimerMultiplier() {return 0.85;}
}
