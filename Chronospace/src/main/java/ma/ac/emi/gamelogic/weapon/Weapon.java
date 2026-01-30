package ma.ac.emi.gamelogic.weapon;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import com.jogamp.opengl.GL3;

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
import ma.ac.emi.glgraphics.GLGraphics;
import ma.ac.emi.glgraphics.Texture;
import ma.ac.emi.input.MouseHandler;
import ma.ac.emi.math.Matrix4;
import ma.ac.emi.math.Vector3D;

@Getter
@Setter
public class Weapon extends Entity{
	private static final String[] ACTIONS = {"Idle", "Attacking_Init", "Attacking", "Reload_Init", "Reload", "Reload_Finish", "Switching"};
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
    
    private Vector3D relativeProjectilePos;
    
    protected AttackStrategy attackStrategy;
        
    public Weapon(WeaponItem weaponItem) {
    	this.weaponItem = weaponItem;
        pos = new Vector3D();
        dir = new Vector3D();
        tsla = 0;
        tssr = 0;
        
        if(weaponItem != null) {
        	WeaponItemDefinition definition = (WeaponItemDefinition) weaponItem.getItemDefinition();
        	attackStrategy = definition.getAttackStrategyDefinition().create();
            setAmmo(definition.getMagazineSize());
        }
        
        this.relativeProjectilePos = new Vector3D();
        setupAnimations();
    }
    
	@Override
	public void initStateMachine() {
		for (String action : ACTIONS) {
			for (String direction : DIRECTIONS) {
				String stateName = action + "_" + direction;
				AnimationState state = new AnimationState(stateName);
				if(
						action.equals("Attacking_Init") ||
						action.equals("Reload_Init") || 
						action.equals("Reload_Finish") || 
						action.equals("Switching")
					) 
					state.setDoesLoop(false);
				stateMachine.addAnimationState(state);
			}
		}
		
		addTransitions(TRIGGER_ATTACK, "Idle", "Attacking_Init");
		addTransitions(TRIGGER_ATTACK, "Attacking_Init", "Attacking");
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
		WeaponItemDefinition.WeaponAnimationDetails animationDetails = definition.getAnimationDetails();
		spriteSheet = new SpriteSheet(AssetsLoader.getSprite(animationDetails.spriteSheetPath), animationDetails.spriteWidth, animationDetails.spriteHeight);

		AnimationState idle_left = stateMachine.getAnimationStateByTitle("Idle_Left");
		AnimationState idle_right = stateMachine.getAnimationStateByTitle("Idle_Right");
		AnimationState attacking_init_left = stateMachine.getAnimationStateByTitle("Attacking_Init_Left");
		AnimationState attacking_init_right = stateMachine.getAnimationStateByTitle("Attacking_Init_Right");
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
		
		
		if(spriteSheet.getSheet() == null) {
			for(AnimationState state : stateMachine.getAnimationStates()) {
				state.addFrame(new Sprite());
			}
			spriteSheet = new SpriteSheet(new Sprite(), 32, 32);
			return;
		}
		
		
		for(Sprite sprite : spriteSheet.getAnimationRow(0, animationDetails.idleLength)) {
			idle_left.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(6, animationDetails.idleLength)) {
			idle_right.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(1, animationDetails.attackingInitLength)) {
			attacking_init_left.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(7, animationDetails.attackingInitLength)) {
			attacking_init_right.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(2, animationDetails.attackingLength)) {
			attacking_left.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(8, animationDetails.attackingLength)) {
			attacking_right.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(3, animationDetails.reloadInitLength)) {
			reload_init_left.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(9, animationDetails.reloadInitLength)) {
			reload_init_right.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(4, animationDetails.reloadLength)) {
			reload_left.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(10, animationDetails.reloadLength)) {
			reload_right.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(5, animationDetails.reloadFinishLength)) {
			reload_finish_left.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(11, animationDetails.reloadFinishLength)) {
			reload_finish_right.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(5, animationDetails.reloadFinishLength)) {
			switching_left.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(11, animationDetails.reloadFinishLength)) {
			switching_right.addFrame(sprite);
		}
		
		
	}
    
    public void attack() {
        if (getAttackStrategy() != null) {
            if(isInState("Idle")) {
            	stateMachine.trigger(TRIGGER_ATTACK);
            }
        	if(isInState("Attacking")) getAttackStrategy().execute(this);

        }
    }
    

	public void stopAttacking() {
		if(isInState("Attacking_Init")) {
			stateMachine.trigger(TRIGGER_ATTACK);
			stateMachine.trigger(TRIGGER_STOP);
		}
		else if(isInState("Attacking")) stateMachine.trigger(TRIGGER_STOP);

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
        	g.drawString(String.valueOf(stateMachine.getCurrentAnimationState().getCurrentFrameIndex()), 0, - 30);
        }else {
        	g.setColor(Color.gray);
        	g.fillRect(0, 0, 16, 8);
        }
        g2d.setTransform(oldTransform);
    }
    
    @Override
    public void drawGL(GL3 gl, GLGraphics glGraphics) {
    	if(getBearer() == null) return;
    	
        // --- 1) Determine sprite ---
        Sprite sprite;
        if (stateMachine.getCurrentAnimationState() != null) {
            sprite = stateMachine.getCurrentAnimationState().getCurrentFrameSprite();
        } else {
            sprite = AssetsLoader.getSprite("default_weapon.png");
        }
        
        if(sprite == null) sprite = new Sprite();
        Texture texture = sprite.getTexture(gl);

        float[] model = new float[16];
        Matrix4.identity(model);

        float px = (float) getBearer().getPos().getX();
        float py = (float) (getBearer().getPos().getY() + getBearer().getWeaponYOffset());
        Matrix4.translate(model, px, py, 0f);

        double theta = getDir() != null ? Math.atan2(getDir().getY(), getDir().getX()) : 0;
        Matrix4.rotateZ(model, (float) theta);

        float wx = (float) getBearer().getWeaponXOffset() - sprite.getWidth() / 2f;
        float wy = -sprite.getHeight() / 2f;
        Matrix4.translate(model, wx, wy, 0f);

        Matrix4.scale(model, sprite.getWidth(), sprite.getHeight(), 1f);
        
        glGraphics.drawSprite(gl, texture, model, null, bearer.getColorCorrection());
    }
    
    
    public void update(double step) {
    	WeaponItemDefinition def = (WeaponItemDefinition)(weaponItem.getItemDefinition());
    	double playSpeed = def.getAnimationDetails().attackingLength*def.getAttackSpeed()/24;
    	stateMachine.getAnimationStateByTitle("Attacking_Right").setPlaySpeed(playSpeed);
    	stateMachine.getAnimationStateByTitle("Attacking_Left").setPlaySpeed(playSpeed);
    	
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
    	if(isInState("Attacking_Init"))
    		if(stateMachine.getCurrentAnimationState().isAnimationDone()) {
    			stateMachine.getCurrentAnimationState().reset();
    			stateMachine.trigger(TRIGGER_ATTACK);
    			stateMachine.getCurrentAnimationState().reset();
    			
    		}
    			
    	
        setPos(getBearer().getPos());
        setTsla(getTsla() + step);
        if (getAmmo() <= 0 && ((WeaponItemDefinition)weaponItem.getItemDefinition()).getMagazineSize() != 0) {
        	if(!isInState("Reload")) {
        		if(stateMachine.getCurrentAnimationState().isAnimationDone()) {
        			stateMachine.getCurrentAnimationState().reset();
        			if(!isInState("Idle")) stateMachine.trigger(TRIGGER_STOP);
        			stateMachine.trigger(TRIGGER_RELOAD);
        		}
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
        if(getDir() != null) {
        	int invert = (int) Math.signum(getDir().dotP(new Vector3D(1, 0)));
        	setRelativeProjectilePos(new Vector3D(
        			def.getRelativeProjectilePosX()+getBearer().getWeaponXOffset(), 
        			(def.getRelativeProjectilePosY()+getBearer().getWeaponYOffset()) * invert)
        		.rotateXY(getDir().getAngle()));
        }
       
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
		return currentState.equals(action+"_Left") || currentState.equals(action+"_Right");
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
	
	public boolean isReloadingAnimation() {
		return isInState("Reload") || isInState("Reload_Init") || isInState("Reload_Finish");
	}



}