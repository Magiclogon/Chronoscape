package ma.ac.emi.world;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.CollisionManager;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.projectile.ProjectileManager;
import ma.ac.emi.gamelogic.wave.WaveManager;
import ma.ac.emi.gamelogic.weapon.model.AK47;
import ma.ac.emi.input.MouseHandler;
import ma.ac.emi.math.Vector2D;

@Getter
@Setter
public class World {
	private int width, height; //in tiles
	private Player player;
	private List<Ennemy> ennemies;
	private WaveManager waveManager;
	private Rectangle bound;
	private ProjectileManager projectileManager;
	private CollisionManager collisionManager;

	public World(int w, int h) {
		width = w; height = h;
		collisionManager = new CollisionManager();
		projectileManager = new ProjectileManager();
		
		AK47 ak = new AK47();
		ak.setProjectileManager(this.projectileManager);
		
		player = new Player(new Vector2D(0,0), 100);
		player.setWeapon(ak);
		
		ennemies = new ArrayList<>();
		ennemies.add(new Ennemy(new Vector2D(100, 100), 50));
		ennemies.add(new Ennemy(new Vector2D(200, 200), 50));
		
		bound = new Rectangle(w*GamePanel.TILE_SIZE, h*GamePanel.TILE_SIZE);
		
		collisionManager.setPlayer(player);
		collisionManager.setEnemies(ennemies);
		collisionManager.setProjectileManager(projectileManager);
	}

	public void update(double step) {
		player.update(step);
		Vector2D playerPos = player.getPos();
		for(Ennemy ennemy : ennemies) {
			ennemy.update(step, playerPos);
		}
		
		projectileManager.update(step, this);
		
		collisionManager.handleCollisions();
	}

	public void draw(Graphics g) {
		g.setColor(Color.black);
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				g.drawRect(j*GamePanel.TILE_SIZE, i*GamePanel.TILE_SIZE, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE); //Added this to see how the world reacts to the camera
			}
		}
		player.draw(g);
		for(Ennemy ennemy : ennemies) {
			ennemy.draw(g);
		}
		projectileManager.draw(g);
		
	}
}
