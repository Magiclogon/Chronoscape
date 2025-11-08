package ma.ac.emi.gamelogic.factory;

import ma.ac.emi.gamelogic.difficulty.DifficultyObserver;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.entity.*;
import ma.ac.emi.math.Vector3D;

public class VampireFactory extends EnnemySpecieFactory implements DifficultyObserver{
	private static VampireFactory instance;
    private VampireFactory() {}

	public static VampireFactory getInstance() {
		if(instance == null) instance = new VampireFactory();
		return instance;
	}

    @Override
    public Ennemy createCommon() {
        CommonEnnemy enemy = new CommonEnnemy(new Vector3D(), 10);
        difficulty.adjustEnemyStats(enemy);
        return enemy;
    }

    @Override
    public Ennemy createSpeedster() {
        SpeedsterEnnemy enemy = new SpeedsterEnnemy(new Vector3D(), 10);
        difficulty.adjustEnemyStats(enemy);
        return enemy;
    }

    @Override
    public Ennemy createTank() {
        TankEnnemy enemy = new TankEnnemy(new Vector3D(), 10);
        difficulty.adjustEnemyStats(enemy);
        return enemy;
    }

    @Override
    public Ennemy createRanged() {
        RangedEnnemy enemy = new RangedEnnemy(new Vector3D(), 10);
        difficulty.adjustEnemyStats(enemy);
        return enemy;
    }

    @Override
    public Ennemy createBoss() {
        BossEnnemy enemy = new BossEnnemy(new Vector3D(), 10);
        difficulty.adjustEnemyStats(enemy);
        return enemy;
    }
    
    @Override
	public void refreshDifficulty(DifficultyStrategy difficulty) {
		setDifficulty(difficulty);
		System.out.println("difficulty set");
	}

}
