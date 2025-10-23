package ma.ac.emi.gamelogic.wave;

import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.factory.EnnemySpecieFactory;

import java.util.ArrayList;
import java.util.List;

public class Wave {
    private int number;
    private List<Ennemy> enemies;
    private EnnemySpecieFactory specieFactory;
    private DifficultyStrategy difficulty;

    public Wave(int number, EnnemySpecieFactory specieFactory, DifficultyStrategy difficulty) {
        this.number = number;
        this.specieFactory = specieFactory;
        this.difficulty = difficulty;
        this.enemies = new ArrayList<>();
    }

    public void spawn() {
        // Spawn enemies logic
    }

    public boolean isCompleted() {
        return enemies.isEmpty();
    }

    public int getNumber() { return number; }
    public List<Ennemy> getEnemies() { return enemies; }
}