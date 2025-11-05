package ma.ac.emi.world;

import java.awt.Graphics;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.attack.manager.AttackObjectManager;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.factory.EnnemySpecieFactory;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.ItemLoader;
import ma.ac.emi.gamelogic.shop.Rarity;
import ma.ac.emi.gamelogic.shop.WeaponItem;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.gamelogic.wave.Wave;
import ma.ac.emi.gamelogic.wave.WaveConfig;
import ma.ac.emi.gamelogic.wave.WaveConfigLoader;
import ma.ac.emi.gamelogic.wave.WaveFactory;
import ma.ac.emi.math.Vector2D;

@Getter
@Setter
public class WorldManager {
	private List<World> worlds;
	private World currentWorld;
	private WaveConfigLoader configLoader;
	private WaveFactory waveFactory;
	private DifficultyStrategy difficulty;
	private EnnemySpecieFactory specieFactory;
	private Player player;
	
	public WorldManager(DifficultyStrategy difficulty, EnnemySpecieFactory factory) {
		this.difficulty = difficulty;
		this.specieFactory = factory;
		this.configLoader = new WaveConfigLoader();
		this.waveFactory = new WaveFactory();
		
		this.player = Player.getInstance();
		
		worlds = new ArrayList<>();
		
		try {
			loadWorldsFromConfig("waves.json");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        currentWorld = worlds.get(0);
        
        WeaponItemDefinition fistsDef = (WeaponItemDefinition) ItemLoader.getInstance().getItemsByRarity().get(Rarity.LEGENDARY).get("fists");
		WeaponItem fists = new WeaponItem(fistsDef);
		
		player.setAttackObjectManager(currentWorld.getAttackObjectManager());
		player.setPos(new Vector2D(GamePanel.TILE_SIZE*currentWorld.getWidth()/2, GamePanel.TILE_SIZE*currentWorld.getHeight()/2));
		player.setSpeed(100);
        player.getInventory().addItem(fists);
        player.getInventory().equipWeapon(fists, 0);
        player.initWeapons();
        
        currentWorld.setPlayer(player);
	}
	
	public void loadWorldsFromConfig(String filepath) throws IOException {
        List<WorldConfig> configs = configLoader.loadWorldsFromFile(filepath);
        
        for(WorldConfig worldConfig: configs) {
        	World world = new World(worldConfig.getWorldWidth(), worldConfig.getWorldHeight());
        	world.setDifficulty(difficulty);
        	world.setSpecieFactory(specieFactory);
        	for (WaveConfig config : worldConfig.getWaves()) {
                Wave wave = waveFactory.createWave(config, difficulty, specieFactory,
                        worldConfig.getWorldWidth(), worldConfig.getWorldHeight());
                wave.setAttackObjectManager(world.getAttackObjectManager());
                world.getWaveManager().addWave(wave);
                world.getPickableManager().subscribe(wave);

            }
            System.out.println("Loaded " + world.getWaveManager().getWaves().size() + " waves from " + filepath);

        	worlds.add(world);
        }
        

    }
	
	public void update(double step) {
		if(currentWorld != null) currentWorld.update(step);
	}
	
	public void draw(Graphics g) {
		if(currentWorld != null) currentWorld.draw(g);
	}
	
	public void nextWorld() {
		int currentIndex = worlds.indexOf(currentWorld);
		System.out.println(currentIndex);
		currentWorld = worlds.get( (currentIndex+ 1) % worlds.size());
		System.out.println("Next " +  worlds.indexOf(currentWorld));
				
		player.setAttackObjectManager(currentWorld.getAttackObjectManager());
		player.setPos(new Vector2D(GamePanel.TILE_SIZE*currentWorld.getWidth()/2, GamePanel.TILE_SIZE*currentWorld.getHeight()/2));
		player.initWeapons();
		currentWorld.setPlayer(player);

	}
}
