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
import ma.ac.emi.tiles.MapTheme;
import ma.ac.emi.tiles.TileManager;
import ma.ac.emi.tiles.TileMap;

@Getter
@Setter
public class WorldManager {
	public static List<WorldConfig> configs;

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
			loadWorldsFromConfig("src/main/resources/configs/waves.json");
		} catch (IOException e) {
			e.printStackTrace();
		}
		currentWorldIndex = -1;

		nextWorld();
		player.init();

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
		configs = configLoader.loadWorldsFromFile(filepath);
		
		for (WorldConfig worldConfig : configs) {
			// Get specie factory
			EnnemySpecieFactory specieFactory = EndlessWorldGenerator.SPECIES.get(worldConfig.getSpecieType());
			
			TileManager tileManager = new TileManager(MapTheme.ROBOTS);
			worldConfig.getMaps().forEach(map -> {
				TileMap tileMap = new TileMap(map);
				tileManager.addMap(tileMap);
			});
			
			// Create world with context pattern
			World world = new World(
				worldConfig.getWorldWidth(),
				worldConfig.getWorldHeight(),
				specieFactory,
				tileManager
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
			
			GameController.getInstance().getGamePanel().removeDrawable(world);
			GameController.getInstance().getGameGLPanel().getRenderer().removeDrawable(world);
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
		if(currentWorld != null) GameController.getInstance().getGamePanel().removeDrawable(currentWorld);
		currentWorldIndex++;

		if (currentWorldIndex < worlds.size()) {
			currentWorld = worlds.get(currentWorldIndex);
		} else {
			currentWorld = endlessGenerator.generateWorld(currentWorldIndex);
		}
		GameController.getInstance().getGamePanel().addDrawable(currentWorld);;
		
		System.out.println("Switched to world with species: " + 
			currentWorld.getSpecieFactory().getClass().getName());
		
		// Setup player for new world
		setupPlayerForWorld();
		
		// Reinitialize player weapons (if needed)
		player.initWeapons();
	}
}