package ma.ac.emi.gamelogic.difficulty;

public interface DifficultyStarategy {
    public interface DifficultyStrategy {
        void adjustEnemyStats(Ennemy enemy);
        void adjustPickableDrop(Pickable pickable);
    }
}
