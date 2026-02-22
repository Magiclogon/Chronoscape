package ma.ac.emi.gamelogic.player;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;
import java.awt.Color;

import javax.swing.SwingUtilities;

import com.jogamp.opengl.GL3;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.fx.AnimationState;
import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.fx.Sprite;
import ma.ac.emi.fx.SpriteSheet;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.particle.ParticleEmitter;
import ma.ac.emi.gamelogic.particle.lifecycle.UndeterminedStrategy;
import ma.ac.emi.gamelogic.physics.AABB;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.gamelogic.weapon.WeaponItemFactory;
import ma.ac.emi.gamelogic.weapon.behavior.WeaponBehaviorDefinition;
import ma.ac.emi.glgraphics.GLGraphics;
import ma.ac.emi.glgraphics.lighting.Light;
import ma.ac.emi.input.KeyHandler;
import ma.ac.emi.input.MouseHandler;
import ma.ac.emi.math.Vector3D;
import ma.ac.emi.gamelogic.shop.*;

@Setter
@Getter
public class Player extends LivingEntity {
	private String pseudoname;
	private double money;
	private double baseLuck;
	private double luck;
	private Gender gender;
	private Inventory inventory;
	private Weapon[] equippedWeapons;
	private int weaponIndex;
	private Weapon activeWeapon;
	private boolean switching;
	
	private static Player instance;
	private PlayerConfig config;
	
	private Player() {
		inventory = new Inventory();
		velocity = new Vector3D();
		hitbox = new AABB(new Vector3D(), new Vector3D(11, 14));
		bound = new AABB(new Vector3D(), new Vector3D(11, 4));
		equippedWeapons = new Weapon[Inventory.MAX_EQU];
		
		hasHands = true;
		if(shadow != null) GameController.getInstance().addDrawable(shadow);
	}
	
	@Override
	public void setupAnimations() {
		stateMachine.clearAllStates();
		spriteSheet = new SpriteSheet(AssetsLoader.getSprite(config.animationDetails.spriteSheetPath), 
				config.animationDetails.spriteWidth, 
				config.animationDetails.spriteHeight);
		
		AnimationState idle_right = stateMachine.getAnimationStateByTitle("Idle_Right");
		AnimationState run_right = stateMachine.getAnimationStateByTitle("Running_Right");
		AnimationState back_right = stateMachine.getAnimationStateByTitle("Backing_Right");
		AnimationState die_right = stateMachine.getAnimationStateByTitle("Dying_Right");
		AnimationState spawn_right = stateMachine.getAnimationStateByTitle("Spawning_Right");
		
		AnimationState idle_left = stateMachine.getAnimationStateByTitle("Idle_Left");
		AnimationState run_left = stateMachine.getAnimationStateByTitle("Running_Left");
		AnimationState back_left = stateMachine.getAnimationStateByTitle("Backing_Left");
		AnimationState die_left = stateMachine.getAnimationStateByTitle("Dying_Left");
		AnimationState spawn_left = stateMachine.getAnimationStateByTitle("Spawning_Left");

		for(Sprite sprite : spriteSheet.getAnimationRow(0, config.animationDetails.idleLength)) {
			idle_right.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(1, config.animationDetails.runningLength)) {
			run_right.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(2, config.animationDetails.backingLength)) {
			back_left.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(3, config.animationDetails.idleLength)) {
			idle_left.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(4, config.animationDetails.runningLength)) {
			run_left.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(5, config.animationDetails.backingLength)) {
			back_right.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(6, config.animationDetails.dyingLength)) {
			die_left.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(7, config.animationDetails.dyingLength)) {
			die_right.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(6, config.animationDetails.spawningLength)) {
			spawn_left.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(7, config.animationDetails.spawningLength)) {
			spawn_right.addFrame(sprite);
		}
	}
	
	public static Player getInstance() {
		if(instance == null) instance = new Player();
		return instance;
	}
	
	@Override
	public void init() {
		baseHP = 100;
		baseHPMax = 100;
		baseSpeed = 200;

		hp = 100;
		money = 10000;
		luck = baseLuck;
		weaponIndex = 0;
		
		weaponXOffset = 9;
		weaponYOffset = 5;
		
		config = PlayerConfigLoader.load("/configs/player_config.json");
		applyBaseStats(config);
		
		setBaseColorCorrection(config.colorCorrection);
		setLightingStrategy(config.lightingStrategy);
		
		behaviors.clear();
		config.behaviorDefinitions.forEach(b -> this.behaviors.add(b.create()));
		
		WeaponItem startingWeaponItem = WeaponItemFactory.getInstance().createWeaponItem(config.startingWeaponId);
		
		getInventory().init();
        getInventory().addItem(startingWeaponItem);
        getInventory().equipWeapon(startingWeaponItem, 0);
        initWeapons();
        
        setupAnimations();
        if(!isIdle()) stateMachine.trigger("Stop");
        
        setLight(new Light(getPos(), 200));
        
        behaviors.forEach(b -> b.onInit(this));
	}
	
	public void applyBaseStats(PlayerConfig cfg) {
	    this.pseudoname = cfg.pseudoname;
	    this.money = cfg.money;

	    this.baseHP = cfg.baseHP;
	    this.baseHPMax = cfg.baseHPMax;
	    this.baseSpeed = cfg.baseSpeed;
	    this.baseStrength = cfg.baseStrength;
	    this.regenerationSpeed = cfg.regenerationSpeed;
		this.baseLuck = cfg.baseLuck;
	    
	    // Initialize current stats from base stats
	    resetBaseStats();
	}


	public void resetBaseStats() {
		this.hpMax = baseHPMax;
	    this.hp = baseHP;
	    this.speed = baseSpeed;
	    this.strength = baseStrength;
		this.luck = baseLuck;
	}
	
	public void initWeapons() {
		for(Weapon weapon: equippedWeapons) {
			if(weapon != null) GameController.getInstance().removeDrawable(weapon);
		}
		for(int i = 0; i < Inventory.MAX_EQU; i++) {
			if(inventory.getEquippedWeapons()[i] == null) {
				equippedWeapons[i] = null;
				continue;
			}
			WeaponItem item = inventory.getEquippedWeapons()[i];
			List<WeaponBehaviorDefinition> behaviors = ((WeaponItemDefinition) item.getItemDefinition()).getBehaviorDefinitions();
			Weapon weapon = new Weapon(item, this);
			
			behaviors.forEach(b -> weapon.getBehaviors().add(b.create()));
			weapon.init();
			
			GameController.getInstance().removeDrawable(weapon);

			weapon.setAttackObjectManager(attackObjectManager);
			weapon.snapTo(this);
			equippedWeapons[i] = weapon;
		}
		setActiveWeapon(equippedWeapons[weaponIndex]);
	}
	
	public void setActiveWeapon(Weapon weapon) {
		this.activeWeapon = weapon;
		if(activeWeapon != null) GameController.getInstance().addDrawable(activeWeapon);
	}

	public double applyLuckToValue(double baseValue) {

		return baseValue * (1.0 + (this.luck * 0.10));
	}
	

	@Override
	public void update(double step) {
		
		if(!isIdle() && !isDying() && !isSpawning()) stateMachine.trigger("Stop");
		
		if(isSpawning()) {
			stateMachine.update(step);

			if(stateMachine.getCurrentAnimationState().isAnimationDone()) {
				if(activeWeapon != null) GameController.getInstance().addDrawable(activeWeapon);
			}
			super.update(step);
			return;
		}

		if(getHp() <= 0) {
			if(activeWeapon != null) GameController.getInstance().removeDrawable(activeWeapon);
			if(!isDying()) stateMachine.trigger("Die");
			if(deathAnimationDone()) {
				stateMachine.getCurrentAnimationState().reset();
				SwingUtilities.invokeLater(() -> GameController.getInstance().showGameOver());
				return;
			}
			stateMachine.update(step);
			return;
		}
				
		if(MouseHandler.getInstance().isMouseDown()) {
			attack(MouseHandler.getInstance().getMouseWorldPos(), step);
		}

		velocity.init();

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
		
		velocity = velocity.normalize().mult(speed);

		
		if(KeyHandler.getInstance().consumeSwitchWeapon()) {
			switching = true;
		}
		if(activeWeapon != null) {
			activeWeapon.update(step);
		}
		
		if(switching) {
			switchWeapons();
		}
		
		
		pointAt(MouseHandler.getInstance().getMouseWorldPos());
		
		changeStateDirection();
		stateMachine.update(step);
		
		getLight().setPosition(getPos());

		super.update(step);

	}
	
	@Override
	public void draw(Graphics g) {
		super.draw(g);
		if(activeWeapon != null)
			activeWeapon.draw(g);
	}
	
	
	@Override
	public void drawGL(GL3 gl, GLGraphics glGraphics) {
		super.drawGL(gl, glGraphics);
//		if(activeWeapon != null && hp > 0)
//			activeWeapon.drawGL(gl, glGraphics);
	}

	public void setWeapon(Weapon weapon) {
		this.activeWeapon = weapon;
		this.activeWeapon.snapTo(this);
	}
	
	public boolean hasWeapon(WeaponItem weaponItem) {
		return inventory.hasItem(weaponItem.getItemDefinition().getId());
	}

	@Override
	public void attack(Vector3D target, double step) {
		if(activeWeapon != null) this.activeWeapon.attack(target, step);
	}
	

	
	@Override
	public void consumeAmmo() {
		if(activeWeapon != null) {
			activeWeapon.setAmmo(activeWeapon.getAmmo()-1);
		}
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

	@Override
	public void switchWeapons() {
		if(activeWeapon != null) {
			activeWeapon.triggerSwitchingOut();
		
			if(activeWeapon.isCurrentAnimationDone()) {
				activeWeapon.resetCurrentAnimation();
				weaponIndex = Math.floorMod(weaponIndex+1, 3);
				
				GameController.getInstance().removeDrawable(activeWeapon);
				activeWeapon = equippedWeapons[weaponIndex];
				if(activeWeapon != null) GameController.getInstance().addDrawable(activeWeapon);
				
				switching = false;
				if(activeWeapon != null) activeWeapon.triggerSwitchingIn();

			}
		}else {
			weaponIndex = Math.floorMod(weaponIndex+1, 3);
			
			activeWeapon = equippedWeapons[weaponIndex];
			if(activeWeapon != null) GameController.getInstance().addDrawable(activeWeapon);
			
			switching = false;
			if(activeWeapon != null) activeWeapon.triggerSwitchingIn();

		}
	}
	
	@Override
	public void addInvincibilityFlashingEffect(double duration, double flashingFrequency) {
		super.addInvincibilityFlashingEffect(duration, flashingFrequency);
		if(activeWeapon != null) {
			activeWeapon.addInvincibilityFlashingEffect(duration, flashingFrequency);
		}
	}
	

}
