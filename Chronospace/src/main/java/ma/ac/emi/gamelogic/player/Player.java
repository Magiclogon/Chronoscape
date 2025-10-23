package ma.ac.emi.gamelogic.player;

import java.awt.*;

import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.entity.Entity;
import ma.ac.emi.input.KeyHandler;
import ma.ac.emi.math.Vector2D;

public class Player extends Entity{
	
	public Player(Vector2D pos, double speed) {
		super(pos, speed);
		vel = new Vector2D();
	}
	

	@Override
	public void update(double step) {
		vel.init();
		if(KeyHandler.getInstance().isLeft()) vel.setX(-1*speed);
		if(KeyHandler.getInstance().isRight()) vel.setX(speed);
		if(KeyHandler.getInstance().isUp()) vel.setY(-1*speed);
		if(KeyHandler.getInstance().isDown()) vel.setY(speed);
		vel.mult(step);
		pos = pos.add(vel);
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.RED);
		g.fillRect((int)(pos.getX()), (int)(pos.getY()), GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
		
	}
	
	
}
