package ma.ac.emi.world;

import java.awt.Graphics;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.factory.EnnemySpecieFactory;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.wave.Wave;
import ma.ac.emi.gamelogic.wave.WaveConfig;
import ma.ac.emi.gamelogic.wave.WaveConfigLoader;
import ma.ac.emi.gamelogic.wave.WaveFactory;
import ma.ac.emi.math.Vector3D;

@Getter
@Setter
public class WorldManager {
	private List<World> worlds;
	private WaveConfigLoader configLoader;
	private WaveFactory waveFactory;
	private Player player;
	
	private World currentWorld;
	private int currentWorldIndex;
	
	private EndlessWorldGenerator endlessGenerator;
	
	public WorldManager(DifficultyStrategy difficulty) {
		this.configLoader = new WaveConfigLoader();
		this.waveFactory = new WaveFactory();
		init();
	}
	
	public void init() {
		// Get difficulty from GameController
		this.player = Player.getInstance();
		
		// Initialize endless generator with difficulty
		this.endlessGenerator = new EndlessWorldGenerator(10, 1.15, waveFactory);
		
		worlds = new ArrayList<>();
		
		try {
			loadWorldsFromConfig("waves.json");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Set up first world
		currentWorldIndex = 0;
		currentWorld = worlds.get(currentWorldIndex);
		
		// Initialize player for the first world
		player.init();
		setupPlayerForWorld();
	}
	
	private void setupPlayerForWorld() {
		if (currentWorld == null) return;
		
		// Set player's attack object manager to current world's
		player.setAttackObjectManager(currentWorld.getAttackObjectManager());
		
		// Position player in center of world
		Vector3D centerPos = new Vector3D(
			GamePanel.TILE_SIZE * currentWorld.getWidth() / 2,
			GamePanel.TILE_SIZE * currentWorld.getHeight() / 2
		);
		player.setPos(centerPos);
		// Set player in world context
		currentWorld.setPlayer(player);
	}
	
	public void loadWorldsFromConfig(String filepath) throws IOException {
		List<WorldConfig> configs = configLoader.loadWorldsFromFile(filepath);
		
		for (WorldConfig worldConfig : configs) {
			// Get specie factory
			EnnemySpecieFactory specieFactory = EndlessWorldGenerator.SPECIES.get(worldConfig.getSpecieType());
			
			// Create world with context pattern
			World world = new World(
				worldConfig.getWorldWidth(),
				worldConfig.getWorldHeight(),
				specieFactory
			);
			
			// Load waves for this world
			for (WaveConfig config : worldConfig.getWaves()) {
				Wave wave = waveFactory.createWave(
					config,
					specieFactory,
					worldConfig.getWorldWidth(),
					worldConfig.getWorldHeight()
				);
				
				// Set attack object manager for wave
				wave.setAttackObjectManager(world.getAttackObjectManager());
				
				// Add wave to world
				world.getWaveManager().addWave(wave);
				
				// Subscribe pickable manager to wave events
				world.getPickableManager().subscribe(wave);
			}
			
			System.out.println("Loaded " + world.getWaveManager().getWaves().size() + " waves from " + filepath);
			worlds.add(world);
		}
	}
	
	public void update(double step) {
		if (currentWorld != null) {
			currentWorld.update(step);
		}
	}
	
	public void draw(Graphics g) {
		if (currentWorld != null) {
			currentWorld.draw(g);
		}
	}
	
	public void nextWorld() {
		currentWorldIndex++;
		
		// Get next world (or generate endless world)
		if (currentWorldIndex < worlds.size()) {
			currentWorld = worlds.get(currentWorldIndex);
		} else {
			currentWorld = endlessGenerator.generateWorld(currentWorldIndex);
		}
		
		System.out.println("Switched to world with species: " + 
			currentWorld.getSpecieFactory().getClass().getName());
		
		// Setup player for new world
		setupPlayerForWorld();
		
		// Reinitialize player weapons (if needed)
		player.initWeapons();
	}
	
}