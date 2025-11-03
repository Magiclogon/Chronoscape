package ma.ac.emi.world;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.CollisionManager;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.ai.MeleeAIBehavior;
import ma.ac.emi.gamelogic.ai.PathFinder;
import ma.ac.emi.gamelogic.ai.RangedAIBehavior;
import ma.ac.emi.gamelogic.attack.manager.AttackObjectManager;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.difficulty.EasyDifficultyStrategy;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.entity.RangedEnnemy;
import ma.ac.emi.gamelogic.entity.SpeedsterEnnemy;
import ma.ac.emi.gamelogic.factory.EnnemySpecieFactory;
import ma.ac.emi.gamelogic.factory.VampireFactory;
import ma.ac.emi.gamelogic.pickable.PickableManager;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.ItemLoader;
import ma.ac.emi.gamelogic.shop.Rarity;
import ma.ac.emi.gamelogic.shop.WeaponItem;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.gamelogic.wave.Wave;
import ma.ac.emi.gamelogic.wave.WaveManager;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.math.Vector2D;

@Getter
@Setter
public class World {
	private int width, height;
	private Player player;
	private WaveManager waveManager;
	private Rectangle bound;
	private AttackObjectManager attackObjectManager;
	private CollisionManager collisionManager;
	private PickableManager pickableManager;
	private PathFinder pathfinder;
	private List<Rectangle> obstacles; // Future use for obstacles

	public World(int w, int h) {
		width = w;
		height = h;

		bound = new Rectangle(w * GamePanel.TILE_SIZE, h * GamePanel.TILE_SIZE);
		obstacles = new ArrayList<>();

		collisionManager = new CollisionManager();
		collisionManager.setWorld(this);
		
		attackObjectManager = new AttackObjectManager(this);
		pickableManager = new PickableManager(this);
		DifficultyStrategy difficulty = new EasyDifficultyStrategy();
		EnnemySpecieFactory specieFactory = new VampireFactory(difficulty);

		waveManager = new WaveManager(difficulty, specieFactory, width, height);
		waveManager.setAttackObjectManager(attackObjectManager);

		// Try to load waves from config file
		try {
			waveManager.loadWavesFromConfig("waves.json");
		} catch (IOException e) {
			System.err.println("Could not load waves.json, creating sample file...");
			try {
				waveManager.createSampleConfigFile("waves.json");
				System.out.println("Sample waves file created. Please configure waves.json");
			} catch (IOException ex) {
				System.err.println("Failed to create sample config: " + ex.getMessage());
			}
		}
		

		// Initialize pathfinder for AI
		pathfinder = new PathFinder(this, GamePanel.TILE_SIZE);
		
		WeaponItemDefinition fistsDef = (WeaponItemDefinition) ItemLoader.getInstance().getItemsByRarity().get(Rarity.LEGENDARY).get("fists");
		WeaponItem fists = new WeaponItem(fistsDef);

		player = Player.getInstance();
		player.setAttackObjectManager(attackObjectManager);
		player.setPos(new Vector2D(GamePanel.TILE_SIZE*w/2, GamePanel.TILE_SIZE*h/2));
		player.setSpeed(100);
        player.getInventory().addItem(fists);
        player.getInventory().equipWeapon(fists, 0);
        player.initWeapons();

		

		// Subscribe each wave
		for (Wave wave : waveManager.getWaves()) {
			pickableManager.subscribe(wave);
		}

		collisionManager.setPlayer(player);
		collisionManager.setEnemies(new ArrayList<Ennemy>());
		collisionManager.setAttackObjectManager(attackObjectManager);
		collisionManager.setPickableManager(pickableManager);

		// initialize AI
		initializeEnemyAI();
	}

	private void initializeEnemyAI() {
		for (Wave wave : waveManager.getWaves()) {
			for (Ennemy enemy : wave.getEnemies()) {
				setupEnemyAI(enemy);
			}
		}
	}

	private void setupEnemyAI(Ennemy enemy) {
		if (enemy instanceof RangedEnnemy) {
			enemy.setAiBehavior(new RangedAIBehavior(pathfinder, 150, 200));
		} else if (enemy instanceof SpeedsterEnnemy) {
			enemy.setAiBehavior(new MeleeAIBehavior(pathfinder, 5));
		} else {
			enemy.setAiBehavior(new MeleeAIBehavior(pathfinder, 5));
		}
	}

	// declare a place as an obstacle
	public void addObstacle(Rectangle obstacle) {
		obstacles.add(obstacle);
	}

	// check if a place contains an obstacle
	public boolean isObstacle(int gridX, int gridY) {

		if (gridX < 0 || gridY < 0 || gridX >= width || gridY >= height) {
			return true;
		}

		Rectangle checkRect = new Rectangle(
				gridX * GamePanel.TILE_SIZE,
				gridY * GamePanel.TILE_SIZE,
				GamePanel.TILE_SIZE,
				GamePanel.TILE_SIZE
		);

		for (Rectangle obstacle : obstacles) {
			if (checkRect.intersects(obstacle)) {
				return true;
			}
		}

		return false;
	}

	public void update(double step) {
		player.update(step);

		// Update wave manager
		waveManager.update(step, player.getPos());

		// ensure enemies have AI
		List<Ennemy> currentEnemies = waveManager.getCurrentEnemies();
		for (Ennemy enemy : currentEnemies) {
			if (enemy.getAiBehavior() == null) {
				setupEnemyAI(enemy);
			}
		}
		collisionManager.setEnemies(currentEnemies);

		attackObjectManager.update(step);
		collisionManager.handleCollisions();
	}

	public void draw(Graphics g) {
		// Draw grid
		g.setColor(Color.black);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				g.drawRect(j * GamePanel.TILE_SIZE, i * GamePanel.TILE_SIZE,
						GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
			}
		}

		// Draw obstacles (if any)
		g.setColor(Color.DARK_GRAY);
		for (Rectangle obstacle : obstacles) {
			g.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
		}

		attackObjectManager.draw(g);

		player.draw(g);
		for (Wave wave : waveManager.getWaves()) {
			for (Ennemy ennemy : wave.getEnemies()) {
				ennemy.draw(g);
			}
		}

		pickableManager.getPickables().forEach(p -> p.draw(g));
	}
}
