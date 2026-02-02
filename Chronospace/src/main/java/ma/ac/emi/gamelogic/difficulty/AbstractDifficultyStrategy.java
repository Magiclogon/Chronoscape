package ma.ac.emi.gamelogic.difficulty;

public abstract class AbstractDifficultyStrategy implements DifficultyStrategy {
    @Override
    public double getEnemyHpMultiplier() { return 1.0; }
    @Override
    public double getEnemyDamageMultiplier() { return 1.0; }
    @Override
    public double getEnemySpeedMultiplier() { return 1.0; }

    @Override
    public double getEnemyCountMultiplier() { return 1.0; }

    @Override
    public double getPickableDropRate() { return 0.5; }
    @Override
    public double getPickableValueMultiplier() { return 1.0; }
}