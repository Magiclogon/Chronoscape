package ma.ac.emi.world;

import java.awt.*;

import ma.ac.emi.camera.Camera;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.math.Vector2D;

public class World {
	private int width, height; //width and height in tiles
	private Camera camera;
	private Player player;
	
	public World(int w, int h) {
		width = w; height = h;
		player = new Player(new Vector2D(), 5);
		camera = new Camera(player.getPos(), width, height);
	}
	
	public void update(double step) {
		player.update(step);
	}
	
	public void draw(Graphics g) {
		g.setColor(Color.BLACK);
		for(int i = 0; i < height; i++) {
			for(int j = 0 ; j < width; j++) {
				g.drawRect(i*GamePanel.TILE_SIZE, j*GamePanel.TILE_SIZE, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
			}
		}
		
		player.draw(g);
	}
}
