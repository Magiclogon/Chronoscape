package ma.ac.emi.gamelogic.factory;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.attack.behavior.BehaviorFactory;
import ma.ac.emi.gamelogic.difficulty.DifficultyObserver;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.entity.behavior.EntityBehaviorDefinition;
import ma.ac.emi.gamelogic.entity.behavior.EntityBehaviorFactory;
import ma.ac.emi.gamelogic.wave.WaveManager;

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
            JsonArray types = root.getAsJsonArray("types");
            Gson gson = new Gson();
            
            for(JsonElement element : types) {
            	JsonObject node = element.getAsJsonObject();
            	            	
            	String type = node.get("type").getAsString();
            	
            	EnemyDefinition def = null;
            	switch(type) {
            	case "common":
            		def = gson.fromJson(node, CommonEnemyDefinition.class);
            		break;
            	
            	case "ranged":
            		def = gson.fromJson(node, RangedEnemyDefinition.class);
            		break;
            		
            	case "speedster":
            		def = gson.fromJson(node, SpeedsterEnemyDefinition.class);
            		break;
            		
            	case "tank":
            		def = gson.fromJson(node, TankEnemyDefinition.class);
            		break;
            		
            	case "boss":
            		def = gson.fromJson(node, BossEnemyDefinition.class);
            		break;
            		
            	default:
            		throw new IllegalArgumentException("Unknown enemy type: " + type);
            	}
            	
            	List<EntityBehaviorDefinition> behaviors = new ArrayList<>();
            	if (node.has("behaviors")) {
                    JsonArray behaviorArray = node.getAsJsonArray("behaviors");
                    for (JsonElement b : behaviorArray) {
                    	JsonObject behaviorJson = b.getAsJsonObject();
                        behaviors.add(EntityBehaviorFactory.create(behaviorJson));
                    }
                }
            	
            	def.setBehaviorDefinitions(behaviors);
            	
            	definitions.put(type, def);
            }
            
            
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

	public abstract Ennemy createCommon(WaveManager manager);
	public abstract Ennemy createSpeedster(WaveManager manager);
	public abstract Ennemy createTank(WaveManager manager);
	public abstract Ennemy createRanged(WaveManager manager);
	public abstract Ennemy createBoss(WaveManager manager);
}