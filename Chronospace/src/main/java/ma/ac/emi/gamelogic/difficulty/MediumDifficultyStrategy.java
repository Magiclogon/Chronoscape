package ma.ac.emi.gamelogic.difficulty;

import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.pickable.Pickable;

public class MediumDifficultyStrategy implements DifficultyStrategy {
    @Override
    public void adjustEnemyStats(Ennemy enemy) {
        // Increase enemy stats for hard mode
    }

    @Override
    public void adjustPickableDrop(Pickable pickable) {
        // Decrease drop rates for hard mode
    }
}