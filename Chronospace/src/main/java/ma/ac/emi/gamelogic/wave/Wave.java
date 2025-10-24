package ma.ac.emi.gamelogic.wave;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.factory.EnnemySpecieFactory;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Wave {
    private int number;
    private int enemiesNumber;
    private List<Ennemy> enemies;
    private EnnemySpecieFactory specieFactory;
    private DifficultyStrategy difficulty;

    public Wave(int number, int enemiesNumber, EnnemySpecieFactory specieFactory, DifficultyStrategy difficulty) {
        this.number = number;
        this.enemiesNumber = enemiesNumber;
        this.specieFactory = specieFactory;
        this.difficulty = difficulty;
        this.enemies = new ArrayList<>();
    }

    public void spawn() {
        enemies.clear();

        for (int i = 0; i < enemiesNumber; i++) {
            Ennemy enemy = specieFactory.createCommon();
            if (enemy != null) {
                enemies.add(enemy);
            }
        }
    }

    public void update(double deltaTime) {
        enemies.removeIf(enemy -> enemy.getHp() <= 0);
    }

    public boolean isCompleted() {
        return enemies.isEmpty();
    }

}