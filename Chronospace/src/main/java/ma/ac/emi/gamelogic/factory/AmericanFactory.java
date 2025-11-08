package ma.ac.emi.gamelogic.factory;

import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.entity.*;
import ma.ac.emi.math.Vector3D;

public class AmericanFactory extends EnnemySpecieFactory{
	private static AmericanFactory instance;
    private AmericanFactory() {}

	public static AmericanFactory getInstance() {
		if(instance == null) instance = new AmericanFactory();
		return instance;
	}

    @Override
    public Ennemy createCommon() {
        CommonEnnemy enemy = new CommonEnnemy(new Vector3D(), 1);
        difficulty.adjustEnemyStats(enemy);
        return enemy;
    }

    @Override
    public Ennemy createSpeedster() {
        SpeedsterEnnemy enemy = new SpeedsterEnnemy(new Vector3D(), 2);
        difficulty.adjustEnemyStats(enemy);
        return enemy;
    }

    @Override
    public Ennemy createTank() {
        TankEnnemy enemy = new TankEnnemy(new Vector3D(), 0.5);
        difficulty.adjustEnemyStats(enemy);
        return enemy;
    }

    @Override
    public Ennemy createRanged() {
        RangedEnnemy enemy = new RangedEnnemy(new Vector3D(), 1);
        difficulty.adjustEnemyStats(enemy);
        return enemy;
    }

    @Override
    public Ennemy createBoss() {
        BossEnnemy enemy = new BossEnnemy(new Vector3D(), 0.5);
        difficulty.adjustEnemyStats(enemy);
        return enemy;
    }
}