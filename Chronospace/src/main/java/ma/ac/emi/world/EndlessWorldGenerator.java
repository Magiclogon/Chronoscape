package ma.ac.emi.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.difficulty.EasyDifficultyStrategy;
import ma.ac.emi.gamelogic.factory.AlienFactory;
import ma.ac.emi.gamelogic.factory.AmericanFactory;
import ma.ac.emi.gamelogic.factory.EnnemySpecieFactory;
import ma.ac.emi.gamelogic.factory.VampireFactory;
import ma.ac.emi.gamelogic.factory.ZombieFactory;
import ma.ac.emi.gamelogic.wave.Wave;
import ma.ac.emi.gamelogic.wave.WaveConfig;
import ma.ac.emi.gamelogic.wave.WaveFactory;

@Getter
@Setter
public class EndlessWorldGenerator {
	public static final Map<String, EnnemySpecieFactory> SPECIES = new HashMap<>();

	static {
		SPECIES.put("vampire", new VampireFactory(new EasyDifficultyStrategy()));
		SPECIES.put("zombie", new ZombieFactory(new EasyDifficultyStrategy()));
		SPECIES.put("american", new AmericanFactory(new EasyDifficultyStrategy()));
		SPECIES.put("alien", new AlienFactory(new EasyDifficultyStrategy()));
    }
	
	public final int MIN_WORLD_WIDTH = 30, MAX_WORLD_WIDTH = 70, MIN_WORLD_HEIGHT = 30, MAX_WORLD_HEIGHT = 70;
	public final int WAVES_PER_WORLD = 10;
	
	private double difficultyMultiplier;
	private int baseEnemyCount;
	private Random random;
	
	private WaveFactory waveFactory;
	private DifficultyStrategy difficulty;
	
	private List<EnnemySpecieFactory> specieFactories;
	
	public EndlessWorldGenerator(int baseEnemyCount, double difficultyMultiplier, WaveFactory waveFactory, DifficultyStrategy difficulty) {
        this.baseEnemyCount = baseEnemyCount;
        this.difficultyMultiplier = difficultyMultiplier;
        this.waveFactory = waveFactory;
        this.difficulty = difficulty;
        this.random = new Random();
        
        for(EnnemySpecieFactory factory : SPECIES.values()) {
        	factory.setDifficulty(difficulty);
        }

        initSpeciesList();
    }
	
	public World generateWorld(int currentWorldIndex) {
		if(specieFactories.isEmpty()) {
			initSpeciesList();
		}
		EnnemySpecieFactory specieFactory = specieFactories.remove(random.nextInt(specieFactories.size()));
		int worldWidth = random.nextInt(MAX_WORLD_WIDTH-MIN_WORLD_WIDTH)+MIN_WORLD_WIDTH;
		int worldHeight = random.nextInt(MAX_WORLD_HEIGHT-MIN_WORLD_HEIGHT)+MIN_WORLD_HEIGHT;
		World world = new World(worldWidth, worldHeight, specieFactory);
		world.setSpecieFactory(specieFactory);
		
		for(int i = 0; i < WAVES_PER_WORLD; i++) {
			Wave wave = waveFactory.createWave(generateWave(i, currentWorldIndex), world.getSpecieFactory(), worldWidth, worldHeight);
			wave.setAttackObjectManager(world.getAttackObjectManager());
            world.getWaveManager().addWave(wave);
            world.getPickableManager().subscribe(wave);
		}
		
		return world;
	}
	
	public WaveConfig generateWave(int waveNumber, int currentWorldIndex) {
        WaveConfig wave = new WaveConfig();
        wave.setWaveNumber(waveNumber);

        // Dynamic spawn rate
        wave.setSpawnDelay((float) Math.max(0.1, 0.6 - (waveNumber * 0.01)));

        // Enemy composition based on difficulty
        Map<String, Integer> enemies = new HashMap<>();

        int totalEnemies = (int) (baseEnemyCount * Math.pow(difficultyMultiplier, waveNumber+currentWorldIndex*WAVES_PER_WORLD));

        // Weighted distribution of enemy types
        enemies.put("common", (int) (totalEnemies * 0.5));
        enemies.put("ranged", (int) (totalEnemies * 0.2));
        enemies.put("speedster", (int) (totalEnemies * 0.2));
        enemies.put("tank", (int) (totalEnemies * 0.1));
        if (waveNumber == WAVES_PER_WORLD-1) enemies.put("boss", 1); // boss every 10 waves

        wave.setEnemies(enemies);
        wave.setBossWave(waveNumber == WAVES_PER_WORLD-1);

        return wave;
    }
	
	private void initSpeciesList() {
		specieFactories = new ArrayList<>();
        specieFactories.addAll(SPECIES.values());
	}
}
