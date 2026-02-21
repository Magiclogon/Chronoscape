package ma.ac.emi.gamelogic.difficulty;

public interface DifficultyStrategy {
    // Enemy stats adjustements
    double getEnemyHpMultiplier();
    double getEnemyDamageMultiplier();
    double getEnemySpeedMultiplier();

    // wave ennemy number adjustements
    double getEnemyCountMultiplier();

    // loot pickables adjustements
    double getPickableDropRate();
    double getPickableValueMultiplier();

    // boss spawn adjustments
    double getBossSpawnCountMultiplier();
    double getBossSpawnRateMultiplier();
}