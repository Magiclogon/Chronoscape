package ma.ac.emi.world;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.CollisionManager;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.attack.manager.AttackObjectManager;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.difficulty.EasyDifficultyStrategy;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.factory.EnnemySpecieFactory;
import ma.ac.emi.gamelogic.factory.VampireFactory;
import ma.ac.emi.gamelogic.pickable.PickableManager;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.wave.Wave;
import ma.ac.emi.gamelogic.wave.WaveManager;
import ma.ac.emi.gamelogic.weapon.model.AK47;
import ma.ac.emi.gamelogic.weapon.model.RPG7;
import ma.ac.emi.gamelogic.weapon.model.Spear;
import ma.ac.emi.gamelogic.weapon.model.Sword;
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

	public World(int w, int h) {
		width = w;
		height = h;
		bound = new Rectangle(w*GamePanel.TILE_SIZE, h*GamePanel.TILE_SIZE);

		collisionManager = new CollisionManager();
		attackObjectManager = new AttackObjectManager(this);
		pickableManager = new PickableManager(this);

		AK47 ak = new AK47();
		ak.setAttackObjectManager(this.attackObjectManager);

		RPG7 rpg7 = new RPG7();
		rpg7.setAttackObjectManager(this.attackObjectManager);

		Sword sword = new Sword();
		sword.setAttackObjectManager(attackObjectManager);
		
		Spear spear = new Spear();
		spear.setAttackObjectManager(attackObjectManager);

		player = new Player(new Vector2D(w * GamePanel.TILE_SIZE / 2,
				h * GamePanel.TILE_SIZE / 2), 100);
		player.setWeapon(ak);

		DifficultyStrategy difficulty = new EasyDifficultyStrategy();
		EnnemySpecieFactory specieFactory = new VampireFactory(difficulty);

		waveManager = new WaveManager(difficulty, specieFactory, width, height);

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

		// Subscribe each wave
		for (Wave wave : waveManager.getWaves()) {
			pickableManager.subscribe(wave);
		}

		collisionManager.setPlayer(player);
		collisionManager.setEnemies(new ArrayList<Ennemy>());
		collisionManager.setAttackObjectManager(attackObjectManager);
		collisionManager.setPickableManager(pickableManager);
	}

	public void update(double step) {
		player.update(step);

		// Update wave manager
		waveManager.update(step, player.getPos());

		// Get enemies from current wave
		collisionManager.setEnemies(waveManager.getCurrentEnemies());

		attackObjectManager.update(step);
		collisionManager.handleCollisions();
	}

	public void draw(Graphics g) {
		g.setColor(Color.black);
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				g.drawRect(j*GamePanel.TILE_SIZE, i*GamePanel.TILE_SIZE,
						GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
			}
		}

		attackObjectManager.draw(g);

		player.draw(g);
		for(Wave wave: waveManager.getWaves()) {
			for(Ennemy ennemy : wave.getEnemies()) {
				ennemy.draw(g);
			}
		}

		pickableManager.getPickables().forEach(p -> p.draw(g));
	}
}
