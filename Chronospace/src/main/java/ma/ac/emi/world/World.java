package ma.ac.emi.world;

import java.awt.*;

import ma.ac.emi.camera.Camera;
import ma.ac.emi.camera.GameDrawable;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.math.Vector2D;

public class World extends GameDrawable{
	private int width, height; //width and height in tiles
	private Player player;
	
	public World(int w, int h, Camera camera) {
		super(camera);
		width = w; height = h;
		player = new Player(new Vector2D(), 2, camera);
		
		camera.snapTo(player);
	}
	
	public void update(double step) {
		camTransform();
		player.update(step);
	}
	
	public void draw(Graphics g) {
		g.setColor(Color.BLACK);
		for(int i = 0; i < height; i++) {
			for(int j = 0 ; j < width; j++) {
				g.drawRect((int)(getScreenPos().getX()+j*GamePanel.TILE_SIZE*scaleRatios.getX()),
						(int)(getScreenPos().getY()+i*GamePanel.TILE_SIZE*scaleRatios.getY()),
						(int)(GamePanel.TILE_SIZE*scaleRatios.getX()), 
						(int)(GamePanel.TILE_SIZE*scaleRatios.getY()));
			}
		}
		
		player.draw(g);
	}

	@Override
	public void camTransform() {
		setScreenPos(camera.camTransform(new Vector2D()));
		setScaleRatios(camera.getScreenCamRatios());
		
	}
}
