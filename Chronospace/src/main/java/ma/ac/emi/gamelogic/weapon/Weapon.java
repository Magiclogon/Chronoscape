package ma.ac.emi.gamelogic.weapon;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.fx.AnimationState;
import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.fx.Sprite;
import ma.ac.emi.fx.SpriteSheet;
import ma.ac.emi.gamelogic.attack.manager.AttackObjectManager;
import ma.ac.emi.gamelogic.entity.Entity;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.WeaponItem;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.input.MouseHandler;
import ma.ac.emi.math.Vector3D;

@Getter
@Setter
public class Weapon extends Entity{
	private static final String[] ACTIONS = {"Idle", "Attacking", "Reload_Init", "Reload", "Reload_Finish", "Switching"};
	private static final String[] DIRECTIONS = {"Left", "Right"};
	
	private static final String TRIGGER_STOP = "Stop";
	private static final String TRIGGER_ATTACK = "Attack";
	private static final String TRIGGER_RELOAD = "Reload";
	private static final String TRIGGER_SWITCH = "Switch";
	protected WeaponItem weaponItem;
	
    private Vector3D dir;
    private LivingEntity bearer;
    private double tsla;
    private int ammo;
    private double tssr;
    private AttackObjectManager attackObjectManager;
    
    protected AttackStrategy attackStrategy;
        
    public Weapon(WeaponItem weaponItem) {
    	this.weaponItem = weaponItem;
        pos = new Vector3D();
        dir = new Vector3D();
        tsla = 0;
        tssr = 0;
        
        if(weaponItem != null) {
        	WeaponItemDefinition definition = (WeaponItemDefinition) weaponItem.getItemDefinition();
        	attackStrategy = WeaponStrategies.STRATEGIES.get(definition.getAttackStrategy());
            setAmmo(definition.getMagazineSize());
        }
        
        setupAnimations();
    }
    
	@Override
	public void initStateMachine() {
		for (String action : ACTIONS) {
			for (String direction : DIRECTIONS) {
				String stateName = action + "_" + direction;
				AnimationState state = new AnimationState(stateName);
				if(action.equals("Reload_Init") || action.equals("Reload_Finish") || action.equals("Switching")) state.setDoesLoop(false);
				stateMachine.addAnimationState(state);
			}
		}
		
		addTransitions(TRIGGER_ATTACK, "Idle", "Attacking");
		addTransitions(TRIGGER_STOP, "Attacking", "Idle");
		addTransitions(TRIGGER_RELOAD, "Idle", "Reload_Init");
		addTransitions(TRIGGER_RELOAD, "Reload_Init", "Reload");
		addTransitions(TRIGGER_STOP, "Reload", "Reload_Finish");
		addTransitions(TRIGGER_STOP, "Reload_Finish", "Idle");
		addTransitions(TRIGGER_SWITCH, "Idle", "Switching");
		addTransitions(TRIGGER_STOP, "Switching", "Idle");
		
		addLookTransitions();
		
		stateMachine.setDefaultState("Switching_Right");
		
	}
	
	private void addTransitions(String trigger, String fromAction, String toAction) {
		for (String direction : DIRECTIONS) {
			stateMachine.addStateTransfer(trigger, 
				fromAction + "_" + direction, 
				toAction + "_" + direction);
		}
	}
	
	private void addLookTransitions() {
		for(String action: ACTIONS) {
			stateMachine.addStateTransfer("Look_Left", action+"_Right", action+"_Left");
			stateMachine.addStateTransfer("Look_Right", action+"_Left", action+"_Right");
		}
	}

	@Override
	public void setupAnimations() {
		WeaponItemDefinition definition = (WeaponItemDefinition)(getWeaponItem().getItemDefinition());
		spriteSheet = new SpriteSheet(AssetsLoader.getSprite(definition.getSpriteSheetPath()), definition.getSpriteWidth(), definition.getSpriteHeight());
		if(spriteSheet.getSheet() == null) return;
		
		AnimationState idle_left = stateMachine.getAnimationStateByTitle("Idle_Left");
		AnimationState idle_right = stateMachine.getAnimationStateByTitle("Idle_Right");
		AnimationState attacking_left = stateMachine.getAnimationStateByTitle("Attacking_Left");
		AnimationState attacking_right = stateMachine.getAnimationStateByTitle("Attacking_Right");
		AnimationState reload_init_left = stateMachine.getAnimationStateByTitle("Reload_Init_Left");
		AnimationState reload_init_right = stateMachine.getAnimationStateByTitle("Reload_Init_Right");
		AnimationState reload_left = stateMachine.getAnimationStateByTitle("Reload_Left");
		AnimationState reload_right = stateMachine.getAnimationStateByTitle("Reload_Right");
		AnimationState reload_finish_left = stateMachine.getAnimationStateByTitle("Reload_Finish_Left");
		AnimationState reload_finish_right = stateMachine.getAnimationStateByTitle("Reload_Finish_Right");
		AnimationState switching_left = stateMachine.getAnimationStateByTitle("Switching_Left");
		AnimationState switching_right = stateMachine.getAnimationStateByTitle("Switching_Right");

		
		for(Sprite sprite : spriteSheet.getAnimationRow(0, 1)) {
			idle_left.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(5, 1)) {
			idle_right.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(1, 8)) {
			attacking_left.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(6, 8)) {
			attacking_right.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(2, 12)) {
			reload_init_left.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(7, 12)) {
			reload_init_right.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(3, 4)) {
			reload_left.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(8, 4)) {
			reload_right.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(4, 12)) {
			reload_finish_left.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(9, 12)) {
			reload_finish_right.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(4, 12)) {
			switching_left.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(9, 12)) {
			switching_right.addFrame(sprite);
		}
		
		/*
		for(Sprite sprite : spriteSheet.getAnimationRow(0, 1)) {
			idle_left.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(5, 1)) {
			idle_right.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(1, 17)) {
			attacking_left.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(6, 17)) {
			attacking_right.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(2, 0)) {
			reload_init_left.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(7, 0)) {
			reload_init_right.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(3, 0)) {
			reload_left.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(8, 0)) {
			reload_right.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(4, 11)) {
			reload_finish_left.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(9, 11)) {
			reload_finish_right.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(4, 11)) {
			switching_left.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(9, 11)) {
			switching_right.addFrame(sprite);
		}
		*/
	}
    
    public void attack() {
        if (getAttackStrategy() != null) {
            getAttackStrategy().execute(this);
            if(isInState("Idle")) stateMachine.trigger(TRIGGER_ATTACK);
        }
    }
    

	public void stopAttacking() {
		if(isInState("Attacking")) stateMachine.trigger(TRIGGER_STOP);
	}
    
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform oldTransform = g2d.getTransform();
        double theta = getDir()!=null? Math.atan2(getDir().getY(), getDir().getX()) : 0;
        g2d.translate(getPos().getX(), getPos().getY()+getBearer().getWeaponYOffset());
        g2d.rotate(theta);

        if(stateMachine.getCurrentAnimationState() != null) {
        	BufferedImage sprite = stateMachine.getCurrentAnimationState().getCurrentFrameSprite().getSprite();
        	g.drawImage(sprite, (int)(getBearer().getWeaponXOffset()-sprite.getWidth()/2), (int)(-sprite.getHeight()/2), null);
        }else {
        	g.setColor(Color.gray);
        	g.fillRect(0, 0, 16, 8);
        }
        g2d.setTransform(oldTransform);
    }
    
    public void update(double step) {
    	WeaponItemDefinition def = (WeaponItemDefinition)(weaponItem.getItemDefinition());
    	stateMachine.getAnimationStateByTitle("Attacking_Right").setPlaySpeed(17*def.getAttackSpeed()/24);
    	stateMachine.getAnimationStateByTitle("Attacking_Left").setPlaySpeed(17*def.getAttackSpeed()/24);
    	if(isInState("Reload_Init"))
    		if(stateMachine.getCurrentAnimationState().isAnimationDone()) {
    			stateMachine.getCurrentAnimationState().reset();
    			stateMachine.trigger(TRIGGER_RELOAD);
    		}
    	if(isInState("Reload_Finish"))
    		if(stateMachine.getCurrentAnimationState().isAnimationDone()) {
    			stateMachine.getCurrentAnimationState().reset();
    			stateMachine.trigger(TRIGGER_STOP);
    		}
    	if(isInState("Switching"))
    		if(stateMachine.getCurrentAnimationState().isAnimationDone()) {
    			stateMachine.getCurrentAnimationState().reset();
    			stateMachine.trigger(TRIGGER_STOP);

    		}
    			
    	
        setPos(getBearer().getPos());
        setTsla(getTsla() + step);
        
        if (getAmmo() == 0 && ((WeaponItemDefinition)weaponItem.getItemDefinition()).getMagazineSize() != 0) {
        	System.out.println("ammo == 0");
        	if(!isInState("Reload")) {
            	if(!isInState("Idle")) stateMachine.trigger(TRIGGER_STOP);
            	stateMachine.trigger(TRIGGER_RELOAD);
        	}

            if(stateMachine.getCurrentAnimationState().getTitle().equals("Reload_Left") ||
            	stateMachine.getCurrentAnimationState().getTitle().equals("Reload_Right")) setTssr(getTssr() + step);
        }
        
        if (tssr >= ((WeaponItemDefinition)weaponItem.getItemDefinition()).getReloadingTime() && 
        		((WeaponItemDefinition)weaponItem.getItemDefinition()).getMagazineSize() != 0) {
        	stateMachine.trigger(TRIGGER_STOP);
            setAmmo(((WeaponItemDefinition)weaponItem.getItemDefinition()).getMagazineSize());
            setTssr(0);
        }
        
        setDir(getBearer().getDir());
        
        changeStateDirection();
        stateMachine.update(step);
    }
    
    public void snapTo(LivingEntity entity) {
        setBearer(entity);
    }

    public void reload() {
        if (ammo == ((WeaponItemDefinition)weaponItem.getItemDefinition()).getMagazineSize() || tssr > 0) return;
        System.out.println("Reloading weapon...");
        tssr = 0;
    }
    
    public boolean isFromPlayer() {
        return getBearer() instanceof Player;
    }

    public boolean isInState(String action) {
		String currentState = stateMachine.getCurrentAnimationState().getTitle();
		return currentState.startsWith(action + "_");
	}
    
    private boolean isLooking(String dir) {
    	String currentState = stateMachine.getCurrentAnimationState().getTitle();
    	return currentState.endsWith("_"+dir);
    }
    
    private void changeStateDirection() {
    	if(getDir() == null) return;
    	double dp_left = getDir().dotP(new Vector3D(-1, 0));
    	double dp_right = getDir().dotP(new Vector3D(1, 0));
    	
    	if(dp_left > dp_right) {
    		if(!isLooking("Left")) stateMachine.trigger("Look_Left");
    	}else {
    		if(!isLooking("Right")) stateMachine.trigger("Look_Right");
    	}
    }

	public void consumeAmmo() {
		bearer.consumeAmmo();
	}
	
	public void triggerSwitching() {
		if(!isInState("Idle")) {
			stateMachine.trigger(TRIGGER_STOP);
		}
		stateMachine.trigger(TRIGGER_SWITCH);
	}


}