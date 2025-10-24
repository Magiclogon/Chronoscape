package ma.ac.emi.gamelogic.difficulty;

import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.pickable.Pickable;
import ma.ac.emi.gamelogic.shop.StatModifier;
import ma.ac.emi.gamelogic.wave.Wave;

public class EasyDifficultyStrategy implements DifficultyStrategy {
    @Override
    public void adjustEnemyStats(Ennemy enemy) {
        // Increase enemy stats for hard mode
    }

    @Override
    public void adjustPickableDrop(Pickable pickable) {
        // Decrease drop rates for hard mode
    }

    @Override
    public void adjustEnemiesNumberWave(Wave wave) {
        // Increase enemy number
    }
}
