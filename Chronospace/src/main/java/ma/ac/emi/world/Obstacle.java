package ma.ac.emi.world;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

import com.jogamp.opengl.GL3;

import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.fx.Sprite;
import ma.ac.emi.fx.SpriteSheet;
import ma.ac.emi.gamecontrol.GameObject;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.glgraphics.GLGraphics;
import ma.ac.emi.glgraphics.Texture;
import ma.ac.emi.glgraphics.TextureCache;
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
			if(dir == TileType.LEFT_EDGE_IN || dir == TileType.RIGHT_EDGE_OUT)
				setHitbox(new Rectangle((int)(getPos().getX()+8-GamePanel.TILE_SIZE/2), (int)(getPos().getY()), (int)(GamePanel.TILE_SIZE)/2, (int)(GamePanel.TILE_SIZE)));
			else if(dir == TileType.RIGHT_EDGE_IN || dir == TileType.LEFT_EDGE_OUT)
				setHitbox(new Rectangle((int)(getPos().getX()-8+GamePanel.TILE_SIZE/2), (int)(getPos().getY()), (int)(GamePanel.TILE_SIZE)/2, (int)(GamePanel.TILE_SIZE)));
			else if(dir == TileType.TOP_EDGE_IN || dir == TileType.BOTTOM_EDGE_OUT || 
					dir == TileType.BOTTOM_EDGE_IN || dir == TileType.TOP_EDGE_OUT ||
					dir == TileType.TOP_LEFT_IN || dir == TileType.TOP_RIGHT_IN)
				setHitbox(new Rectangle((int)(getPos().getX()), (int)(getPos().getY()-8+GamePanel.TILE_SIZE/2), GamePanel.TILE_SIZE, (int)(GamePanel.TILE_SIZE)/2));
			else if(dir == TileType.BOTTOM_LEFT_IN || dir == TileType.BOTTOM_RIGHT_IN ||
					dir == TileType.BOTTOM_LEFT_OUT || dir == TileType.BOTTOM_RIGHT_OUT) {
				setHitbox(new Rectangle((int)(getPos().getX()), (int)(getPos().getY()), GamePanel.TILE_SIZE, GamePanel.TILE_SIZE));
			}
			else 
				setHitbox(new Rectangle());
		}
	}
	@Override
	public void draw(Graphics g) {
		g.drawImage(getSpriteSheet().getSprite(0,0,GamePanel.TILE_SIZE,GamePanel.TILE_SIZE).getSprite(),
				(int)(getPos().getX()-GamePanel.TILE_SIZE/2), 
				(int)(getPos().getY()-GamePanel.TILE_SIZE/2),
				null);
		
	}

	@Override
	public void update(double step) {
		// TODO Auto-generated method stub
		
	}
	
	public static boolean isPositionInObstacles(Vector3D pos, List<Obstacle> obstacles) {
		for (Obstacle obstacle : obstacles) {
			if (obstacle.getHitbox() != null &&
					obstacle.getHitbox().contains(pos.getX(), pos.getY())) {
				return true;
			}
		}
		return false;
	}
	@Override
	public void drawGL(GL3 gl, GLGraphics glGraphics) {
		Sprite sprite = getSpriteSheet().getSprite(0, 0, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
		Texture texture = sprite.getTexture(gl);
		glGraphics.drawSprite(gl, texture,
				(int)(getPos().getX()-GamePanel.TILE_SIZE/2), 
				(int)(getPos().getY()-GamePanel.TILE_SIZE/2),
				GamePanel.TILE_SIZE,
				GamePanel.TILE_SIZE
			);
	}
}
