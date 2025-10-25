package ma.ac.emi.world;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.wave.WaveManager;
import ma.ac.emi.gamelogic.weapon.model.AK47;
import ma.ac.emi.input.MouseHandler;
import ma.ac.emi.math.Vector2D;

@Getter
@Setter
public class World {
	private int width;
	private int height;
	private Player player;
	private List<Ennemy> ennemies;
	private WaveManager waveManager;

	public World(int w, int h) {
		width = w; height = h;
		player = new Player(new Vector2D(0,0), 2);
		player.setWeapon(new AK47());
		ennemies = new ArrayList<>();
		ennemies.add(new Ennemy(new Vector2D(100, 100), 1));
		ennemies.add(new Ennemy(new Vector2D(200, 200), 1));
	}

	public void update(double step) {
		player.update(step);
		Vector2D playerPos = player.getPos();
		for(Ennemy ennemy : ennemies) {
			ennemy.update(step, playerPos);
		}
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
		
		
		
		
		
	}
}
