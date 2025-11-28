package ma.ac.emi.world;

import java.awt.*;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.CollisionManager;
import ma.ac.emi.gamecontrol.GameObject;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.ai.*;
import ma.ac.emi.gamelogic.attack.manager.AttackObjectManager;
import ma.ac.emi.gamelogic.entity.*;
import ma.ac.emi.gamelogic.factory.EnnemySpecieFactory;
import ma.ac.emi.gamelogic.pickable.PickableManager;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.gamelogic.wave.Wave;
import ma.ac.emi.gamelogic.wave.WaveManager;
import ma.ac.emi.math.Vector3D;

@Getter
@Setter
public class World extends GameObject{
	private final WorldContext context;
	private final CollisionManager collisionManager;

	public World(int width, int height, EnnemySpecieFactory specieFactory) {
		super(false);
		// Create context with all shared data
		context = new WorldContext(width, height, specieFactory);

		// Initialize managers that depend on context
		initializeManagers();

		// Initialize collision manager
		collisionManager = new CollisionManager(context);
	}

	private void initializeManagers() {
		// Create attack object manager
		AttackObjectManager attackObjectManager = new AttackObjectManager(context);
		context.setAttackObjectManager(attackObjectManager);
		
		// Create pickable manager
		PickableManager pickableManager = new PickableManager(context);
		context.setPickableManager(pickableManager);

		// Create wave manager
		WaveManager waveManager = new WaveManager(context.getWorldWidth(), context.getWorldHeight());
		waveManager.setAttackObjectManager(attackObjectManager);
		context.setWaveManager(waveManager);

		// Initialize pathfinder for AI
		PathFinder pathfinder = new PathFinder(context, GamePanel.TILE_SIZE);
		context.setPathFinder(pathfinder);
	}

	public void setPlayer(Player player) {
		context.setPlayer(player);
		initializeEnemyAI();
	}

	private void initializeEnemyAI() {
		for (Wave wave : context.getWaveManager().getWaves()) {
			for (Ennemy enemy : wave.getEnemies()) {
				setupEnemyAI(enemy);
			}
		}
	}

	private void setupEnemyAI(Ennemy enemy) {
		PathFinder pathfinder = context.getPathFinder();
		
		if (enemy instanceof RangedEnnemy) {
			enemy.setAiBehavior(new RangedAIBehavior(
				pathfinder, 
				150, 
				((WeaponItemDefinition) enemy.getWeapon().getWeaponItem().getItemDefinition()).getRange()
			));
		} else if (enemy instanceof SpeedsterEnnemy) {
			enemy.setAiBehavior(new MeleeAIBehavior(
				pathfinder, 
				((WeaponItemDefinition) enemy.getWeapon().getWeaponItem().getItemDefinition()).getRange()
			));
		} else if (enemy instanceof BossEnnemy) {
			enemy.setAiBehavior(new AlienBossAIBehavior(pathfinder));
		}else {
			enemy.setAiBehavior(new MeleeAIBehavior(
				pathfinder, 
				((WeaponItemDefinition) enemy.getWeapon().getWeaponItem().getItemDefinition()).getRange()
			));
		}
	}

	// Delegate to context
	public void addObstacle(Rectangle obstacle) {
		context.addObstacle(obstacle);
	}

	public boolean isObstacle(int gridX, int gridY) {
		return context.isObstacle(gridX, gridY);
	}

	public void update(double step) {
		Player player = context.getPlayer();
		if (player != null) {
			player.update(step);
		}

		// Update wave manager
		WaveManager waveManager = context.getWaveManager();
		waveManager.update(step, player.getPos());

		// Ensure enemies have AI
		List<Ennemy> currentEnemies = waveManager.getCurrentEnemies();
		for (Ennemy enemy : currentEnemies) {
			if (enemy.getAiBehavior() == null) {
				setupEnemyAI(enemy);
			}

			if (enemy.getAiBehavior() instanceof AlienBossAIBehavior bossAI) {
				if (bossAI.isSpawningNow()) {
					spawnBossMinions(enemy.getPos());
				}
			}
		}
		
		context.getPickableManager().update(step);
		// Update managers
		context.getAttackObjectManager().update(step);
		collisionManager.handleCollisions();
	}

	private void spawnBossMinions(Vector3D bossPos) {
		int minionCount = 3;

		for (int i = 0; i < minionCount; i++) {

			Ennemy minion = context.getSpecieFactory().createCommon();
			minion.setAttackObjectManager(context.getAttackObjectManager());
			minion.initWeapon();

			double offsetX = (Math.random() - 0.5) * 100;
			double offsetY = (Math.random() - 0.5) * 100;
			minion.setPos(bossPos.add(new Vector3D(offsetX, offsetY)));

			setupEnemyAI(minion);

			context.getWaveManager().addEnemyToCurrentWave(minion);
		}

		System.out.println("Boss spawned " + minionCount + " minions!");
	}

	public void draw(Graphics g) {
		int width = context.getWorldWidth();
		int height = context.getWorldHeight();
		
		// Draw grid
		g.setColor(Color.black);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				g.drawRect(j * GamePanel.TILE_SIZE, i * GamePanel.TILE_SIZE,
						GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
			}
		}
	}
	
	// Convenience getters that delegate to context
	public int getWidth() {
		return context.getWorldWidth();
	}
	
	public int getHeight() {
		return context.getWorldHeight();
	}
	
	public Player getPlayer() {
		return context.getPlayer();
	}
	
	public WaveManager getWaveManager() {
		return context.getWaveManager();
	}
	
	public AttackObjectManager getAttackObjectManager() {
		return context.getAttackObjectManager();
	}
	
	public PickableManager getPickableManager() {
		return context.getPickableManager();
	}
	
	public EnnemySpecieFactory getSpecieFactory() {
		return context.getSpecieFactory();
	}
	
	public void setSpecieFactory(EnnemySpecieFactory factory) {
		context.setSpecieFactory(factory);
	}
}