package ma.ac.emi.gamelogic.player;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Color;

import javax.swing.SwingUtilities;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.fx.AnimationState;
import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.fx.Frame;
import ma.ac.emi.fx.Sprite;
import ma.ac.emi.fx.SpriteSheet;
import ma.ac.emi.fx.StateMachine;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamecontrol.GameTime;
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
		
		spriteSheet = new SpriteSheet(AssetsLoader.getSprite("player/running animation test.png"), 
				GamePanel.TILE_SIZE, 
				GamePanel.TILE_SIZE);
		setupAnimations();
	}
	
	private void setupAnimations() {
		AnimationState idle_right = stateMachine.getAnimationStateByTitle("Idle_Right");
		AnimationState run_right = stateMachine.getAnimationStateByTitle("Running_Right");
		
		for(Sprite sprite : spriteSheet.getAnimationRow(0, 6)) {
			idle_right.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(1, 6)) {
			run_right.addFrame(sprite);
		}
	}
	
	public static Player getInstance() {
		if(instance == null) instance = new Player();
		return instance;
	}
	
	public void init() {
		hp = 50;
		hpMax = 100;
		money = 10000;
		speed = 200;
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
			if(getEquippedWeapons()[i] == null) continue;
			GameController.getInstance().getGamePanel().removeDrawable(equippedWeapons[i]);
		}
		for(int i = 0; i < Inventory.MAX_EQU; i++) {
			if(inventory.getEquippedWeapons()[i] == null) continue;
			Weapon weapon = new Weapon(inventory.getEquippedWeapons()[i]);
			weapon.setAttackObjectManager(attackObjectManager);
			weapon.snapTo(this);
			equippedWeapons[i] = weapon;
		}
		setActiveWeapon(equippedWeapons[weaponIndex]);
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
		if(!isIdle()) stateMachine.trigger("Stop");
		
		if(KeyHandler.getInstance().isUp()) {
			if(!isIdle()) stateMachine.trigger("Stop");
			stateMachine.trigger("Run");
			velocity.setY(-1*speed);
		}
		if(KeyHandler.getInstance().isDown()) {
			if(!isIdle()) stateMachine.trigger("Stop");
			stateMachine.trigger("Run");
			velocity.setY(speed);
		}
		if(KeyHandler.getInstance().isLeft()) {
			if(!isIdle()) stateMachine.trigger("Stop");
			stateMachine.trigger("Run");
			velocity.setX(-1*speed);
		}
		if(KeyHandler.getInstance().isRight()) {
			if(!isIdle()) stateMachine.trigger("Stop");
			stateMachine.trigger("Run");
			velocity.setX(speed);
		}
		
		setPos(pos.add(velocity.mult(step)));
		
		bound.x = (int) getPos().getX();
		bound.y = (int) getPos().getY();
		
		if(KeyHandler.getInstance().consumeSwitchWeapon()) {
			GameController.getInstance().getGamePanel().removeDrawable(equippedWeapons[weaponIndex]);
			weaponIndex = Math.floorMod(weaponIndex+1, 3);
			GameController.getInstance().getGamePanel().addDrawable(equippedWeapons[weaponIndex]);

		}
		if(activeWeapon != null) {
			activeWeapon.pointAt(MouseHandler.getInstance().getMouseWorldPos());
			activeWeapon.update(step);
		}
		
		if (!isIdle()) {
		    GameController.getInstance().getParticleSystem().spawnEffect("smoke", pos, this, GameTime.get());
		}
		
		changeStateDirection();
		stateMachine.update(step);
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.GREEN);
		g.fillRect((int)(pos.getX()), (int)(pos.getY()), GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
		
		g.setColor(Color.black);
		g.drawString(stateMachine.getCurrentAnimationState().getTitle(), (int)(pos.getX()), (int)(pos.getY()));
		
		g.drawString(String.valueOf(stateMachine.getCurrentAnimationState().getCurrentFrameIndex()), (int)(pos.getX()), (int)(pos.getY()+10));
		
		g.drawImage(stateMachine.getCurrentAnimationState().getCurrentFrameSprite().getSprite(), (int)(pos.getX()), (int)(pos.getY()), null);
	}
	
	public void setWeapon(Weapon weapon) {
		this.activeWeapon = weapon;
		this.activeWeapon.snapTo(this);
	}
	
	public boolean hasWeapon(WeaponItem weaponItem) {
		return inventory.hasItem(weaponItem.getItemDefinition().getId());
	}

	@Override
	public void attack() {
		if(activeWeapon != null) this.activeWeapon.attack();
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
