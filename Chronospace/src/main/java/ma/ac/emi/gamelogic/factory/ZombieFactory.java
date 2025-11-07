package ma.ac.emi.gamelogic.factory;

import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.entity.*;
import ma.ac.emi.math.Vector2D;

public class ZombieFactory extends EnnemySpecieFactory {
    public ZombieFactory(DifficultyStrategy difficulty) {
		super(difficulty);
		// TODO Auto-generated constructor stub
	}

    @Override
    public Ennemy createCommon() {
        CommonEnnemy enemy = new CommonEnnemy(new Vector2D(), 1);
        difficulty.adjustEnemyStats(enemy);
        return enemy;
    }

    @Override
    public Ennemy createSpeedster() {
        SpeedsterEnnemy enemy = new SpeedsterEnnemy(new Vector2D(), 2);
        difficulty.adjustEnemyStats(enemy);
        return enemy;
    }

    @Override
    public Ennemy createTank() {
        TankEnnemy enemy = new TankEnnemy(new Vector2D(), 0.5);
        difficulty.adjustEnemyStats(enemy);
        return enemy;
    }

    @Override
    public Ennemy createRanged() {
        RangedEnnemy enemy = new RangedEnnemy(new Vector2D(), 1);
        difficulty.adjustEnemyStats(enemy);
        return enemy;
    }

    @Override
    public Ennemy createBoss() {
        BossEnnemy enemy = new BossEnnemy(new Vector2D(), 0.5);
        difficulty.adjustEnemyStats(enemy);
        return enemy;
    }

}