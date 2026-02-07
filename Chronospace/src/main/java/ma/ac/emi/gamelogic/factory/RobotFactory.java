package ma.ac.emi.gamelogic.factory;

import ma.ac.emi.gamelogic.entity.*;
import ma.ac.emi.math.Vector3D;

public class RobotFactory extends EnnemySpecieFactory{
	private static RobotFactory instance;
    private RobotFactory() {
    	loadConfig("src/main/resources/configs/robots.json");
    }

	public static RobotFactory getInstance() {
		if(instance == null) instance = new RobotFactory();
		return instance;
	}

    @Override
    public Ennemy createCommon() {
    	CommonEnemyDefinition def = (CommonEnemyDefinition) definitions.get("common");
        CommonEnnemy enemy = new CommonEnnemy(def);
        applyDifficultyStats(enemy);

        return enemy;
    }

    @Override
    public Ennemy createSpeedster() {
    	SpeedsterEnemyDefinition def = (SpeedsterEnemyDefinition) definitions.get("speedster");
        SpeedsterEnnemy enemy = new SpeedsterEnnemy(def);
        applyDifficultyStats(enemy);
        return enemy;
    }

    @Override
    public Ennemy createTank() {
    	TankEnemyDefinition def = (TankEnemyDefinition) definitions.get("tank");
        TankEnnemy enemy = new TankEnnemy(def);
        applyDifficultyStats(enemy);
        return enemy;
    }

    @Override
    public Ennemy createRanged() {
    	RangedEnemyDefinition def = (RangedEnemyDefinition) definitions.get("ranged");
        RangedEnnemy enemy = new RangedEnnemy(def);
        applyDifficultyStats(enemy);
        return enemy;
    }

    @Override
    public Ennemy createBoss() {
    	BossEnemyDefinition def = (BossEnemyDefinition) definitions.get("boss");
        BossEnnemy enemy = new BossEnnemy(def);
        applyDifficultyStats(enemy);
        return enemy;
    }
}