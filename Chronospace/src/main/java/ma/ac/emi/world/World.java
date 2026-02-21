package ma.ac.emi.world;

import java.awt.*;
import java.util.List;

import com.jogamp.opengl.GL3;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.CollisionManager;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamecontrol.GameObject;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.ai.*;
import ma.ac.emi.gamelogic.attack.manager.AttackObjectManager;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.entity.*;
import ma.ac.emi.gamelogic.factory.EnnemySpecieFactory;
import ma.ac.emi.gamelogic.factory.RobotFactory;
import ma.ac.emi.gamelogic.pickable.PickableManager;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.gamelogic.wave.Wave;
import ma.ac.emi.gamelogic.wave.WaveManager;
import ma.ac.emi.glgraphics.GLGraphics;
import ma.ac.emi.glgraphics.lighting.LightObject;
import ma.ac.emi.glgraphics.lighting.LightObjectManager;
import ma.ac.emi.math.Vector3D;
import ma.ac.emi.tiles.MapTheme;
import ma.ac.emi.tiles.TileManager;
import ma.ac.emi.tiles.TileMap;
import ma.ac.emi.tiles.TileType;

@Getter
@Setter
public class World extends GameObject{
	private final WorldContext context;
	private final CollisionManager collisionManager;
	
	public World(int width, int height, EnnemySpecieFactory specieFactory, TileManager tileManager) {
		context = new WorldContext(width, height, specieFactory, tileManager, new LightObjectManager());
		
		initializeManagers();

		collisionManager = new CollisionManager(context);
		
		syncMapObstacles();
		
		getPos().setZ(-0.02);
	}

	private void initializeManagers() {
		// Create attack object manager
		AttackObjectManager attackObjectManager = new AttackObjectManager(context);
		context.setAttackObjectManager(attackObjectManager);
		
		// Create pickable manager
		PickableManager pickableManager = new PickableManager(context);
		context.setPickableManager(pickableManager);

		// Create wave manager
		WaveManager waveManager = new WaveManager(context);
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
		
		if (enemy instanceof RangedEnnemy || enemy instanceof CommonEnnemy) {
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
			if (context.getSpecieFactory() instanceof RobotFactory) {
				enemy.setAiBehavior(new RobotBossAIBehavior(pathfinder, 
					((WeaponItemDefinition) enemy.getWeapon().getWeaponItem().getItemDefinition()).getRange()
				));
			} else {
				enemy.setAiBehavior(new AlienBossAIBehavior(pathfinder));
			}
		}else {
			enemy.setAiBehavior(new MeleeAIBehavior(
				pathfinder, 
				((WeaponItemDefinition) enemy.getWeapon().getWeaponItem().getItemDefinition()).getRange()
			));
		}
	}

	private void syncMapObstacles() {
		for (int x = 0; x < context.getWorldWidth(); x++) {
			for (int y = 0; y < context.getWorldHeight(); y++) {
				if (context.getTileManager().isSolid(x, y)) {
					context.addObstacle(new Obstacle(
							new Vector3D(
								x * GamePanel.TILE_SIZE,
								y * GamePanel.TILE_SIZE
							),
							context.getTileManager().getCurrentMap().getTile(y, x),
							context.getTileManager()
					));
				}
			}
		}
		
	}

	// Delegate to context
	public void addObstacle(Obstacle obstacle) {
		context.addObstacle(obstacle);
	}

	public boolean isObstacle(int gridX, int gridY) {
		return context.isObstacle(gridX, gridY);
	}

	public void update(double step) {
		// Update wave manager
		WaveManager waveManager = context.getWaveManager();
		waveManager.update(step, Player.getInstance().getPos());

		// Ensure enemies have AI
		List<Ennemy> currentEnemies = waveManager.getCurrentEnemies();
		for (Ennemy enemy : currentEnemies) {
			if (enemy.getAiBehavior() == null) {
				setupEnemyAI(enemy);
			}

			if (enemy.getAiBehavior() instanceof AlienBossAIBehavior bossAI) {
				if (bossAI.isSpawningNow()) {
					spawnBossMinions(enemy.getPos(), bossAI.getSpawnQuantity());
				}
			} else if (enemy.getAiBehavior() instanceof RobotBossAIBehavior robotBossAI) {
				if (robotBossAI.isSpawningNow()) {
					spawnRobotBossMinions(enemy.getPos(), robotBossAI.getSpawnQuantity());
				}
			}
		}
		
		context.getPickableManager().update(step);
		context.getAttackObjectManager().update(step);
		context.getLightObjectManager().update(step);
		
		collisionManager.handleCollisions(step);
	}

	private void spawnBossMinions(Vector3D bossPos, int baseCount) {
		DifficultyStrategy difficulty = GameController.getInstance().getDifficulty();
		double multiplier = difficulty != null ? difficulty.getBossSpawnCountMultiplier() : 1.0;
		int minionCount = (int) (baseCount * multiplier);

		for (int i = 0; i < minionCount; i++) {

			Ennemy minion = context.getSpecieFactory().createCommon();
			minion.setAttackObjectManager(context.getAttackObjectManager());
			minion.initWeapon();

			double offsetX = (Math.random() - 0.5) * 100;
			double offsetY = (Math.random() - 0.5) * 100;
			minion.setPos(clampToWorld(bossPos.add(new Vector3D(offsetX, offsetY))));

			setupEnemyAI(minion);

			GameController.getInstance().addDrawable(minion);
			context.getWaveManager().addEnemyToCurrentWave(minion);
		}

		System.out.println("Boss spawned " + minionCount + " minions!");
	}

	private void spawnRobotBossMinions(Vector3D bossPos, int baseCount) {
		DifficultyStrategy difficulty = GameController.getInstance().getDifficulty();
		double multiplier = difficulty != null ? difficulty.getBossSpawnCountMultiplier() : 1.0;
		int minionCount = (int) (baseCount * multiplier);

		for (int i = 0; i < minionCount; i++) {
			Ennemy minion;
			double rand = Math.random();
			if (rand < 0.2) {
				minion = context.getSpecieFactory().createTank();
			} else if (rand < 0.5) {
				minion = context.getSpecieFactory().createSpeedster();
			} else {
				minion = context.getSpecieFactory().createCommon();
			}

			minion.setAttackObjectManager(context.getAttackObjectManager());
			minion.initWeapon();

			double offsetX = (Math.random() - 0.5) * 150;
			double offsetY = (Math.random() - 0.5) * 150;
			minion.setPos(clampToWorld(bossPos.add(new Vector3D(offsetX, offsetY))));

			setupEnemyAI(minion);

			GameController.getInstance().addDrawable(minion);
			context.getWaveManager().addEnemyToCurrentWave(minion);
		}

		System.out.println("Robot Boss spawned " + minionCount + " minions!");
	}
	

	private Vector3D clampToWorld(Vector3D pos) {
		double margin = GamePanel.TILE_SIZE;
		double maxX = context.getWorldWidth() * GamePanel.TILE_SIZE - margin;
		double maxY = context.getWorldHeight() * GamePanel.TILE_SIZE - margin;

		double x = Math.max(margin, Math.min(maxX, pos.getX()));
		double y = Math.max(margin, Math.min(maxY, pos.getY()));

		return new Vector3D(x, y, pos.getZ());
	}

	public void draw(Graphics g) {
		if (context.getTileManager() != null) {
			context.getTileManager().draw(g);
		}
	}
	
	@Override
	public void drawGL(GL3 gl, GLGraphics glGraphics) {
		if (context.getTileManager() != null) {
			context.getTileManager().setColorCorrection(getColorCorrection());
			context.getTileManager().drawGL(gl, glGraphics);
		}
	}

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
	
	public TileManager getTileManager() {
		return context.getTileManager();
	}

	public Color getVoidColor() {
		return context.getVoidColor();
	}

	public void addLightObject(LightObject lightObject) {
		context.addLightObject(lightObject);
	}
	
	public void removeLightObject(LightObject lightObject) {
		context.removeLightObject(lightObject);
	}

	@Override
	public double getDrawnHeight() {
		return context.getWorldHeight()*GamePanel.TILE_SIZE;
	}

	public void clearAttackObjects() {
		this.getAttackObjectManager().clearObjects();
	}


	
}