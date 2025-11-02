package ma.ac.emi.gamelogic.difficulty;

import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.pickable.Pickable;
import ma.ac.emi.gamelogic.wave.Wave;

public class MediumDifficultyStrategy implements DifficultyStrategy {
    @Override
    public void adjustEnemyStats(Ennemy enemy) {
        // Increase enemy stats for hard mode
    }

    @Override
    public void adjustPickableDrop(Pickable pickable) {
        pickable.adjustForDifficulty(1);
    }

    @Override
    public void adjustEnemiesNumberWave(Wave wave) {
        // Increase enemy number
    }

    @Override
    public double getPickableMultiplier() {
        return 1;
    }
}
