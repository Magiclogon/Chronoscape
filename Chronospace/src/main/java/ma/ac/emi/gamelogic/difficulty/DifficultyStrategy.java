package ma.ac.emi.gamelogic.difficulty;

import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.pickable.Pickable;

public interface DifficultyStrategy {
    void adjustEnemyStats(Ennemy enemy);
    void adjustPickableDrop(Pickable pickable);
}
