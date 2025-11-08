package ma.ac.emi.gamelogic.player;

import java.awt.*;

import javax.swing.SwingUtilities;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.camera.Camera;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.attack.manager.AttackObjectManager;
import ma.ac.emi.gamelogic.entity.Entity;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.input.KeyHandler;
import ma.ac.emi.input.MouseHandler;
import ma.ac.emi.math.Vector3D;
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
		
	private Player() {
		inventory = new Inventory();
		velocity = new Vector3D();
		bound = new Rectangle(GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
		equippedWeapons = new Weapon[Inventory.MAX_EQU];
	}
	
	public static Player getInstance() {
		if(instance == null) instance = new Player();
		return instance;
	}
	
	public void init() {
		hp = 50;
		hpMax = 100;
		money = 10000;
		speed = 100;
		weaponIndex = 0;
		
		WeaponItemDefinition fistsDef = (WeaponItemDefinition) ItemLoader.getInstance().getItemsByRarity().get(Rarity.LEGENDARY).get("fists");
		WeaponItem fists = new WeaponItem(fistsDef);
		
		getInventory().init();
        getInventory().addItem(fists);
        getInventory().equipWeapon(fists, 0);
        initWeapons();
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
		if(getHp() <= 0) {
			SwingUtilities.invokeLater(() -> GameController.getInstance().showGameOver());
		}
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
		
		if(KeyHandler.getInstance().consumeSwitchWeapon()) {
			weaponIndex = Math.floorMod(weaponIndex+1, 3);
		}
		if(equippedWeapons[weaponIndex] != null) {
			equippedWeapons[weaponIndex].pointAt(MouseHandler.getInstance().getMouseWorldPos());
			equippedWeapons[weaponIndex].update(step);
		}
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
	
	public void clamp(Rectangle bound) {
		setPos(new Vector3D(Math.min(bound.width-getBound().width, Math.max(0, getPos().getX())),
				Math.min(bound.height-getBound().height, Math.max(0, getPos().getY()))));
	}
	

}
