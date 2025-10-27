package ma.ac.emi.gamelogic.factory;

import lombok.AllArgsConstructor;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.entity.*;
import ma.ac.emi.math.Vector2D;

@AllArgsConstructor
public class VampireFactory implements EnnemySpecieFactory {
    DifficultyStrategy difficulty;

    @Override
    public Ennemy createCommon() {
        CommonEnnemy enemy = new CommonEnnemy(new Vector2D(), 10);
        difficulty.adjustEnemyStats(enemy);
        return enemy;
    }

    @Override
    public Ennemy createSpeedster() {
        SpeedsterEnnemy enemy = new SpeedsterEnnemy(new Vector2D(), 10);
        difficulty.adjustEnemyStats(enemy);
        return enemy;
    }

    @Override
    public Ennemy createTank() {
        TankEnnemy enemy = new TankEnnemy(new Vector2D(), 10);
        difficulty.adjustEnemyStats(enemy);
        return enemy;
    }

    @Override
    public Ennemy createRanged() {
        RangedEnnemy enemy = new RangedEnnemy(new Vector2D(), 10);
        difficulty.adjustEnemyStats(enemy);
        return enemy;
    }

    @Override
    public Ennemy createBoss() {
        BossEnnemy enemy = new BossEnnemy(new Vector2D(), 10);
        difficulty.adjustEnemyStats(enemy);
        return enemy;
    }
}
