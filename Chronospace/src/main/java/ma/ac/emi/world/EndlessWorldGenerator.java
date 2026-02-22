package ma.ac.emi.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.factory.RobotFactory;
import ma.ac.emi.gamelogic.factory.EnnemySpecieFactory;
import ma.ac.emi.gamelogic.wave.Wave;
import ma.ac.emi.gamelogic.wave.WaveConfig;
import ma.ac.emi.gamelogic.wave.WaveFactory;
import ma.ac.emi.tiles.MapTheme;
import ma.ac.emi.tiles.TileManager;
import ma.ac.emi.tiles.TileMap;

@Getter
@Setter
public class EndlessWorldGenerator {
	public static final Map<String, EnnemySpecieFactory> SPECIES = new HashMap<>();

	static {
		SPECIES.put("robot", RobotFactory.getInstance());

    }
	
	public final int MIN_WORLD_WIDTH = 30, MAX_WORLD_WIDTH = 70, MIN_WORLD_HEIGHT = 30, MAX_WORLD_HEIGHT = 70;
	public final int WAVES_PER_WORLD = 10;
	
	private double difficultyMultiplier;
	private int baseEnemyCount;
	private Random random;
	
	private WaveFactory waveFactory;
	
	private List<EnnemySpecieFactory> specieFactories;
	
	public EndlessWorldGenerator(int baseEnemyCount, double difficultyMultiplier, WaveFactory waveFactory) {
        this.baseEnemyCount = baseEnemyCount;
        this.difficultyMultiplier = difficultyMultiplier;
        this.waveFactory = waveFactory;
        this.random = new Random();
        
        initSpeciesList();
    }
	
	public World generateWorld(int currentWorldIndex) {
		if(specieFactories.isEmpty()) {
			initSpeciesList();
		}
		EnnemySpecieFactory specieFactory = specieFactories.remove(random.nextInt(specieFactories.size()));

		List<WorldConfig> configs = WorldManager.configs;
		WorldConfig worldConfig = configs.get(random.nextInt(configs.size()));
		
		int worldWidth = worldConfig.getWorldWidth();
		int worldHeight = worldConfig.getWorldHeight();
		
		TileManager tileManager = new TileManager(MapTheme.ROBOTS);
		worldConfig.getMaps().forEach(map -> {
			tileManager.addMap(new TileMap(map));
		});
		World world = new World(worldWidth, worldHeight, specieFactory, tileManager);
		world.setSpecieFactory(specieFactory);
		
		for(int i = 0; i < WAVES_PER_WORLD; i++) {
			Wave wave = waveFactory.createWave(generateWave(i, currentWorldIndex), world.getSpecieFactory(), worldWidth, worldHeight, world.getWaveManager());
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
