package ma.ac.emi.gamelogic.difficulty;

public class GameDifficultyManager {
    private DifficultyStrategy strategy;

    public void setStrategy(DifficultyStrategy strategy) {
        this.strategy = strategy;
    }

    public DifficultyStrategy getStrategy() {
        return strategy;
    }
}
