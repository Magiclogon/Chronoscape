package ma.ac.emi.gamelogic.factory;

import ma.ac.emi.gamelogic.entity.*;
import ma.ac.emi.gamelogic.wave.WaveManager;
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
    public Ennemy createCommon(WaveManager manager) {
    	CommonEnemyDefinition def = (CommonEnemyDefinition) definitions.get("common");
        CommonEnnemy enemy = manager.getPool(CommonEnnemy.class).obtain();
        
        enemy.reset(def);
        def.behaviorDefinitions.forEach(b -> enemy.getBehaviors().add(b.create()));
        enemy.init();
        applyDifficultyStats(enemy);
        enemy.setActive(true);
        
        return enemy;
    }

    @Override
    public Ennemy createSpeedster(WaveManager manager) {
    	SpeedsterEnemyDefinition def = (SpeedsterEnemyDefinition) definitions.get("speedster");
        SpeedsterEnnemy enemy = manager.getPool(SpeedsterEnnemy.class).obtain();
        
        enemy.reset(def);
        def.behaviorDefinitions.forEach(b -> enemy.getBehaviors().add(b.create()));
        enemy.init();
        applyDifficultyStats(enemy);
        enemy.setActive(true);
        
        return enemy;
    }

    @Override
    public Ennemy createTank(WaveManager manager) {
    	TankEnemyDefinition def = (TankEnemyDefinition) definitions.get("tank");
        TankEnnemy enemy = manager.getPool(TankEnnemy.class).obtain();

        enemy.reset(def);
        def.behaviorDefinitions.forEach(b -> enemy.getBehaviors().add(b.create()));
        enemy.init();
        applyDifficultyStats(enemy);
        enemy.setActive(true);
        
        return enemy;
    }

    @Override
    public Ennemy createRanged(WaveManager manager) {
    	RangedEnemyDefinition def = (RangedEnemyDefinition) definitions.get("ranged");
        RangedEnnemy enemy = manager.getPool(RangedEnnemy.class).obtain();

        enemy.reset(def);
        def.behaviorDefinitions.forEach(b -> enemy.getBehaviors().add(b.create()));
        enemy.init();
        applyDifficultyStats(enemy);
        enemy.setActive(true);
        
        return enemy;
    }

    @Override
    public Ennemy createBoss(WaveManager manager) {
    	BossEnemyDefinition def = (BossEnemyDefinition) definitions.get("boss");
        BossEnnemy enemy = manager.getPool(BossEnnemy.class).obtain();

        enemy.reset(def);
        def.behaviorDefinitions.forEach(b -> enemy.getBehaviors().add(b.create()));
        enemy.init();
        applyDifficultyStats(enemy);
        enemy.setActive(true);
        
        return enemy;
    }
}