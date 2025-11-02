package ma.ac.emi.gamelogic.player;

import java.awt.*;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.camera.Camera;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.entity.Entity;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.shop.Inventory;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.input.KeyHandler;
import ma.ac.emi.input.MouseHandler;
import ma.ac.emi.math.Vector2D;

@Setter
@Getter
public class Player extends LivingEntity {
	private String pseudoname;
	private double money;
	private Gender gender;
	private Inventory inventory;
	private Weapon weapon, secondaryWeapon, meleeWeapon;
	
	private static Player instance;

	private Player() {
		inventory = new Inventory();
		velocity = new Vector2D();
		hp = 50;
		hpMax = 100;
		money = 0;
		bound = new Rectangle(GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
	}
	
	public static Player getInstance() {
		if(instance == null) instance = new Player();
		return instance;
	}

	@Override
	public void update(double step) {
		if(MouseHandler.getInstance().isMouseDown()) {
			attack();
		}
		
		velocity.init();
		if(KeyHandler.getInstance().isLeft()) velocity.setX(-1*speed);
		if(KeyHandler.getInstance().isRight()) {
			velocity.setX(speed);
		}
		if(KeyHandler.getInstance().isUp()) velocity.setY(-1*speed);
		if(KeyHandler.getInstance().isDown()) velocity.setY(speed);
		setPos(pos.add(velocity.mult(step)));
		
		bound.x = (int) getPos().getX();
		bound.y = (int) getPos().getY();
		if(weapon != null) weapon.update(step);
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.GREEN);
		g.fillRect((int)(pos.getX()), (int)(pos.getY()), GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
		
		if(weapon != null) weapon.draw(g);
	}
	
	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
		this.weapon.snapTo(this);
	}
	
	public boolean hasWeapon(Weapon weapon) {
		return this.weapon.equals(weapon) || 
				this.secondaryWeapon.equals(weapon) ||
				this.meleeWeapon.equals(weapon);
	}

	@Override
	public void attack() {
		if(weapon != null) this.weapon.attack();
	}
	

}
