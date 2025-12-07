package ma.ac.emi.world;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.fx.Sprite;
import ma.ac.emi.fx.SpriteSheet;
import ma.ac.emi.gamecontrol.GameObject;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.math.Vector3D;
import ma.ac.emi.tiles.MapTheme;
import ma.ac.emi.tiles.TileManager;
import ma.ac.emi.tiles.TileType;

public class Obstacle extends GameObject{
	private final TileType direction;
	private final TileManager tileManager;
	
	public Obstacle(Vector3D pos, TileType dir, TileManager tileManager) {
		this.pos = pos;
		this.direction = dir;
		this.tileManager = tileManager;
		
		if(direction != null) {
			Sprite sprite = tileManager.getTileSprites().get(direction);
			if(sprite == null) sprite = AssetsLoader.getSprite("default_sprite.png");
			setSpriteSheet(new SpriteSheet(sprite,
					GamePanel.TILE_SIZE,
					GamePanel.TILE_SIZE
					));

			setBound(new Rectangle((int)(getPos().getX()), (int)(getPos().getY()), GamePanel.TILE_SIZE, GamePanel.TILE_SIZE));
		}
	}
	@Override
	public void draw(Graphics g) {
		g.drawImage(getSpriteSheet().getSprite(0,0,GamePanel.TILE_SIZE,GamePanel.TILE_SIZE).getSprite(),
				(int)(getPos().getX()), 
				(int)(getPos().getY()),
				null);
		
	}

	@Override
	public void update(double step) {
		// TODO Auto-generated method stub
		
	}

}
