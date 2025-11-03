package ma.ac.emi.gamelogic.player;

import java.awt.*;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.camera.Camera;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.attack.manager.AttackObjectManager;
import ma.ac.emi.gamelogic.entity.Entity;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.shop.Inventory;
import ma.ac.emi.gamelogic.shop.ShopItem;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.input.KeyHandler;
import ma.ac.emi.input.MouseHandler;
import ma.ac.emi.math.Vector2D;
import ma.ac.emi.gamelogic.shop.*;

@Setter
@Getter
public class Player extends LivingEntity {
	private String pseudoname;
	private double money;
	private Gender gender;
	private Inventory inventory;
	private Weapon[] equippedWeapons;
	private int weaponIndex;
	
	private static Player instance;
	
	private AttackObjectManager attackObjectManager;

	private Player() {
		inventory = new Inventory();
		velocity = new Vector2D();
		hp = 50;
		hpMax = 100;
		money = 1000;
		bound = new Rectangle(GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
		weaponIndex = 0;
		
		equippedWeapons = new Weapon[Inventory.MAX_EQU];
	}
	
	public static Player getInstance() {
		if(instance == null) instance = new Player();
		return instance;
	}
	
	public void initWeapons() {
		for(int i = 0; i < Inventory.MAX_EQU; i++) {
			if(inventory.getEquippedWeapons()[i] == null) continue;
			Weapon weapon = new Weapon(inventory.getEquippedWeapons()[i]);
			weapon.setAttackObjectManager(attackObjectManager);
			weapon.snapTo(this);
			equippedWeapons[i] = weapon;
		}
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
		
		weaponIndex = Math.floorMod(MouseHandler.getInstance().getMouseWheelRot(), 3);
		if(equippedWeapons[weaponIndex] != null) equippedWeapons[weaponIndex].update(step);
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.GREEN);
		g.fillRect((int)(pos.getX()), (int)(pos.getY()), GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
		
		if(equippedWeapons[weaponIndex] != null) equippedWeapons[weaponIndex].draw(g);
	}
	
	public void setWeapon(Weapon weapon) {
		this.equippedWeapons[weaponIndex] = weapon;
		this.equippedWeapons[weaponIndex].snapTo(this);
	}
	
	public boolean hasWeapon(WeaponItem weaponItem) {
		return inventory.hasItem(weaponItem.getItemDefinition().getId());
	}

	@Override
	public void attack() {
		if(equippedWeapons[weaponIndex] != null) this.equippedWeapons[weaponIndex].attack();
	}

	public Integer isWeaponEquipped(WeaponItem item) {
		for(int i = 0; i < Inventory.MAX_EQU; i++) {
			if(getInventory().getEquippedWeapons()[i] == null) continue;
			if(getInventory().getEquippedWeapons()[i].equals(item)) {
				return i;
			}
		}
		return null;
	}
	

}
