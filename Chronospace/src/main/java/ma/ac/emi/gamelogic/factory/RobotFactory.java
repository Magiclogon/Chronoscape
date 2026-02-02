package ma.ac.emi.gamelogic.factory;

import ma.ac.emi.gamelogic.entity.*;
import ma.ac.emi.math.Vector3D;

public class RobotFactory extends EnnemySpecieFactory{
	private static RobotFactory instance;
    private RobotFactory() {}

	public static RobotFactory getInstance() {
		if(instance == null) instance = new RobotFactory();
		return instance;
	}

    @Override
    public Ennemy createCommon() {
        CommonEnnemy enemy = new CommonEnnemy(new Vector3D(), 115);
        applyDifficultyStats(enemy);

        return enemy;
    }

    @Override
    public Ennemy createSpeedster() {
        SpeedsterEnnemy enemy = new SpeedsterEnnemy(new Vector3D(), 210);
        applyDifficultyStats(enemy);
        return enemy;
    }

    @Override
    public Ennemy createTank() {
        TankEnnemy enemy = new TankEnnemy(new Vector3D(), 90);
        applyDifficultyStats(enemy);
        return enemy;
    }

    @Override
    public Ennemy createRanged() {
        RangedEnnemy enemy = new RangedEnnemy(new Vector3D(), 130);
        applyDifficultyStats(enemy);
        return enemy;
    }

    @Override
    public Ennemy createBoss() {
        BossEnnemy enemy = new BossEnnemy(new Vector3D(), 40);
        applyDifficultyStats(enemy);
        return enemy;
    }
}