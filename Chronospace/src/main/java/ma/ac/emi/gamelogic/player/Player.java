package ma.ac.emi.gamelogic.player;

import java.awt.*;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.camera.Camera;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.entity.Entity;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.shop.Inventory;
import ma.ac.emi.input.KeyHandler;
import ma.ac.emi.math.Vector2D;

@Setter
@Getter
public class Player extends LivingEntity {
	private String pseudoname;
	private double money;
	private Gender gender;
	private Inventory inventory;


	public Player(Vector2D pos, double speed) {
		super(pos, speed);
		inventory = new Inventory();
		velocity = new Vector2D();
		hp = 50;
		hpMax = 100;
		money = 0;
	}

	@Override
	public void update(double step) {
		velocity.init();
		if(KeyHandler.getInstance().isLeft()) velocity.setX(-1*speed);
		if(KeyHandler.getInstance().isRight()) {
			velocity.setX(speed);
		}
		if(KeyHandler.getInstance().isUp()) velocity.setY(-1*speed);
		if(KeyHandler.getInstance().isDown()) velocity.setY(speed);
		velocity.mult(step);
		setPos(pos.add(velocity));
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.GREEN);
		g.fillRect((int)(pos.getX()), (int)(pos.getY()), GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
	}
}
