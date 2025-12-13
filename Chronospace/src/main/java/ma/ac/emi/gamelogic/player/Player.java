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
	private Weapon activeWeapon;
	
	private static Player instance;
		
	private Player() {
		inventory = new Inventory();
		velocity = new Vector3D();
		hitbox = new Rectangle(22, 28);
		bound = new Rectangle(22, 8);
		equippedWeapons = new Weapon[Inventory.MAX_EQU];
	}
	
	@Override
	public void setupAnimations() {
		spriteSheet = new SpriteSheet(AssetsLoader.getSprite("player/main_character-Sheet.png"), 
				GamePanel.TILE_SIZE, 
				GamePanel.TILE_SIZE);
		
		AnimationState idle_right = stateMachine.getAnimationStateByTitle("Idle_Right");
		AnimationState run_right = stateMachine.getAnimationStateByTitle("Running_Right");
		AnimationState back_right = stateMachine.getAnimationStateByTitle("Backing_Right");
		AnimationState die_right = stateMachine.getAnimationStateByTitle("Dying_Right");
		
		AnimationState idle_left = stateMachine.getAnimationStateByTitle("Idle_Left");
		AnimationState run_left = stateMachine.getAnimationStateByTitle("Running_Left");
		AnimationState back_left = stateMachine.getAnimationStateByTitle("Backing_Left");
		AnimationState die_left = stateMachine.getAnimationStateByTitle("Dying_Left");

		for(Sprite sprite : spriteSheet.getAnimationRow(0, 14)) {
			idle_right.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(1, 12)) {
			run_right.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(2, 12)) {
			back_left.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(3, 14)) {
			idle_left.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(4, 12)) {
			run_left.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(5, 12)) {
			back_right.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(6, 49)) {
			die_left.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(7, 49)) {
			die_right.addFrame(sprite);
		}
	}
	
	public static Player getInstance() {
		if(instance == null) instance = new Player();
		return instance;
	}
	
	public void init() {
		baseHP = 100;
		baseHPMax = 100;
		baseSpeed = 200;

		resetBaseStats();
		hp = 100;
		money = 10000;
		weaponIndex = 0;
		
		WeaponItemDefinition fistsDef = (WeaponItemDefinition) ItemLoader.getInstance().getItemsByRarity().get(Rarity.LEGENDARY).get("fists");
		WeaponItem fists = new WeaponItem(fistsDef);
		
		getInventory().init();
        getInventory().addItem(fists);
        getInventory().equipWeapon(fists, 0);
        initWeapons();
        if(!isIdle()) stateMachine.trigger("Stop");
	}

	public void resetBaseStats() {
		hpMax = baseHPMax;
		speed = baseSpeed;
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
		velocity.init();
		if(!isIdle() && !isDying()) stateMachine.trigger("Stop");
		if(getHp() <= 0) {
			if(!isDying()) stateMachine.trigger("Die");
			stateMachine.update(step);
			if(deathAnimationDone()) SwingUtilities.invokeLater(() -> GameController.getInstance().showGameOver());
			return;
		}
		if(MouseHandler.getInstance().isMouseDown()) {
			attack();
		}

		if(KeyHandler.getInstance().isUp()) {
			if(!isIdle() && !isDying()) stateMachine.trigger("Stop");
			stateMachine.trigger("Run");
			velocity.setY(-1);
		}
		if(KeyHandler.getInstance().isDown()) {
			if(!isIdle() && !isDying()) stateMachine.trigger("Stop");
			stateMachine.trigger("Run");
			velocity.setY(1);
		}
		if(KeyHandler.getInstance().isLeft()) {
			if(!isIdle() && !isDying()) stateMachine.trigger("Stop");
			stateMachine.trigger("Run");
			velocity.setX(-1);
		}
		if(KeyHandler.getInstance().isRight()) {
			if(!isIdle()) stateMachine.trigger("Stop");
			stateMachine.trigger("Run");
			velocity.setX(1);
		}
		
		
		if(velocity.norm() != 0) velocity = velocity.normalize().mult(speed);

		
		if(KeyHandler.getInstance().consumeSwitchWeapon()) {
			if(activeWeapon != null) GameController.getInstance().getGamePanel().removeDrawable(activeWeapon);
			weaponIndex = Math.floorMod(weaponIndex+1, 3);
			activeWeapon = equippedWeapons[weaponIndex];
			if(activeWeapon != null) GameController.getInstance().getGamePanel().addDrawable(activeWeapon);

		}
		if(activeWeapon != null) {
			activeWeapon.update(step);
		}
		
		if (!isIdle()) {
		    GameController.getInstance().getParticleSystem().spawnEffect("smoke", pos, this, GameTime.get());
		}
		
		pointAt(MouseHandler.getInstance().getMouseWorldPos());
		
		changeStateDirection();
		stateMachine.update(step);
		
		
		super.update(step);
		

		
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
	

}
