package ma.ac.emi.gamelogic.factory;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.difficulty.DifficultyObserver;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.entity.Ennemy;

public abstract class EnnemySpecieFactory implements DifficultyObserver {
	protected DifficultyStrategy currentDifficulty;
	protected Map<String, EnemyDefinition> definitions;

	protected EnnemySpecieFactory() {
		GameController.getInstance().addDifficultyObserver(this);
		this.currentDifficulty = GameController.getInstance().getDifficulty();
		
		definitions = new HashMap<>();
	}
	
	protected void loadConfig(String path) {
		definitions.clear();
		try (FileReader reader = new FileReader(path)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            Gson gson = new Gson();
            
            JsonObject commonDefObj = root.get("common").getAsJsonObject();
            CommonEnemyDefinition commonDef = gson.fromJson(commonDefObj, CommonEnemyDefinition.class);
            
            JsonObject rangedDefObj = root.get("ranged").getAsJsonObject();
            RangedEnemyDefinition rangedDef = gson.fromJson(rangedDefObj, RangedEnemyDefinition.class);
            
            JsonObject speedsterDefObj = root.get("speedster").getAsJsonObject();
            SpeedsterEnemyDefinition speedsterDef = gson.fromJson(speedsterDefObj, SpeedsterEnemyDefinition.class);
            
            JsonObject tankDefObj = root.get("tank").getAsJsonObject();
            TankEnemyDefinition tankDef = gson.fromJson(tankDefObj, TankEnemyDefinition.class);
            
            JsonObject bossDefObj = root.get("boss").getAsJsonObject();
            BossEnemyDefinition bossDef = gson.fromJson(bossDefObj, BossEnemyDefinition.class);
            
            definitions.put("common", commonDef);
            definitions.put("ranged", rangedDef);
            definitions.put("speedster", speedsterDef);
            definitions.put("tank", tankDef);
            definitions.put("boss", bossDef);
            
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public void refreshDifficulty(DifficultyStrategy difficulty) {
		this.currentDifficulty = difficulty;
	}

	// Applique la difficulté aux ennemies crées
	protected void applyDifficultyStats(Ennemy enemy) {
		if (currentDifficulty == null) return;

		enemy.setHpMax(enemy.getHpMax() * currentDifficulty.getEnemyHpMultiplier());
		enemy.setHp(enemy.getHpMax());
		enemy.setDamage(enemy.getDamage() * currentDifficulty.getEnemyDamageMultiplier());
		enemy.setSpeed(enemy.getSpeed() * currentDifficulty.getEnemySpeedMultiplier());
	}

	public abstract Ennemy createCommon();
	public abstract Ennemy createSpeedster();
	public abstract Ennemy createTank();
	public abstract Ennemy createRanged();
	public abstract Ennemy createBoss();
}