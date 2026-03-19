package ma.ac.emi.gamelogic.difficulty;

public class EasyDifficultyStrategy extends AbstractDifficultyStrategy {
    @Override
    public double getEnemyHpMultiplier() { return 0.8; }

    @Override
    public double getEnemyDamageMultiplier() { return 0.8; }

    @Override
    public double getPickableDropRate() { return 1.2; }

    @Override
    public double getEnemyCountMultiplier() { return 1; }

    @Override
    public double getBossSpawnCountMultiplier() { return 0.8; }
    @Override
    public double getBossSpawnRateMultiplier() { return 0.8; }
}