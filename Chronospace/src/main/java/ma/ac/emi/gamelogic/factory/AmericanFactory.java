package ma.ac.emi.gamelogic.factory;

import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.entity.*;

public class AmericanFactory implements EnnemySpecieFactory {
    private DifficultyStrategy difficulty;

    public AmericanFactory(DifficultyStrategy difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public Ennemy createCommon() {
        CommonEnnemy enemy = new CommonEnnemy();
        difficulty.adjustEnemyStats(enemy);
        return enemy;
    }

    @Override
    public Ennemy createSpeedster() {
        SpeedsterEnnemy enemy = new SpeedsterEnnemy();
        difficulty.adjustEnemyStats(enemy);
        return enemy;
    }

    @Override
    public Ennemy createTank() {
        TankEnnemy enemy = new TankEnnemy();
        difficulty.adjustEnemyStats(enemy);
        return enemy;
    }

    @Override
    public Ennemy createRanged() {
        RangedEnnemy enemy = new RangedEnnemy();
        difficulty.adjustEnemyStats(enemy);
        return enemy;
    }

    @Override
    public Ennemy createBoss() {
        BossEnnemy enemy = new BossEnnemy();
        difficulty.adjustEnemyStats(enemy);
        return enemy;
    }
}