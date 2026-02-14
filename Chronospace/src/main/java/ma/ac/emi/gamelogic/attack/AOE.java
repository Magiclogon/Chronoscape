package ma.ac.emi.gamelogic.attack;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.jogamp.opengl.GL3;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.fx.Sprite;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.attack.type.AOEDefinition;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.physics.AABB;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.glgraphics.GLGraphics;
import ma.ac.emi.math.Vector3D;

@Getter
@Setter
public class AOE extends AttackObject{
	private double age, lastAge;
    private double frameTimer;

    private int frameIndex;
    private AOEPhase phase;

    private AOEDefinition definition;
    private AOEAnimation animation;

    public AOE() {
        this.phase = AOEPhase.INIT;
        this.age = 0;
        this.lastAge = 0;
        this.frameIndex = 0;
        this.frameTimer = 0;
        this.pos.setZ(-0.01);

        if(this.shadow != null) {
        	GameController.getInstance().removeDrawable(shadow);
        	shadow = null;
        }
    }
    
    public void reset(AOEDefinition def, Vector3D pos, Weapon weapon) {
    	setPos(pos);
    	setWeapon(weapon);
        this.definition = def;

        this.animation = AOEAnimationCache.get(def);
        this.phase = AOEPhase.INIT;
        this.age = 0;
        this.lastAge = 0;
        this.frameIndex = 0;
        this.frameTimer = 0;
        
        this.hitbox = new AABB(pos, new Vector3D(def.getAnimationDetails().spriteWidth/2, def.getAnimationDetails().spriteHeight/2));
        this.pos.setZ(-0.01);
    }

    public void update(double step) {
    	super.update(step);
    	hitbox.center = pos;
		lastAge = age;
        age += step;
        frameTimer += step;

        double frameTime = 1.0/24;
        if (frameTimer >= frameTime) {
            frameTimer = 0;
            frameIndex++;
        }

        switch (phase) {
            case INIT -> {
                if (frameIndex >= animation.initFrames.length) {
                    frameIndex = (int) (Math.random() * (animation.loopFrames.length));
                    phase = AOEPhase.LOOP;
                }
            }
            case LOOP -> {
                if (age >= definition.getAgeMax()) {
                    frameIndex = 0;
                    phase = AOEPhase.FINISH;
                } else {
                    frameIndex %= animation.loopFrames.length;
                }
            }
            case FINISH -> {
                if (frameIndex >= animation.finishFrames.length) {
                    this.setActive(false);
                }
            }
        }

    }

    public void draw(Graphics g) {
        Sprite sprite = getCurrentSprite();
        if (sprite == null) return;
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(
            sprite.getSprite(),
            (int) (pos.getX() - definition.getAnimationDetails().spriteWidth / 2),
            (int) (pos.getY() - definition.getAnimationDetails().spriteHeight / 2 - pos.getZ()),
            definition.getAnimationDetails().spriteWidth,
            definition.getAnimationDetails().spriteHeight,
            null
        );
    }
    
    @Override
    public void drawGL(GL3 gl, GLGraphics glGraphics) {
    	Sprite sprite = getCurrentSprite();
    	if (sprite == null) return;
    	glGraphics.drawSprite(gl, sprite.getTexture(gl),
    			(int) (pos.getX() - definition.getAnimationDetails().spriteWidth / 2),
    			(int) (pos.getY() - definition.getAnimationDetails().spriteHeight / 2 - pos.getZ()),
    			definition.getAnimationDetails().spriteWidth,
    			definition.getAnimationDetails().spriteHeight,
    			getColorCorrection()
    		);
    }
    
    @Override
    public Sprite getCurrentSprite() {
    	if(phase == null) return null;
    	if(animation == null) return null;
    	
        return switch (phase) {
            case INIT -> animation.initFrames[Math.min(frameIndex, animation.initFrames.length - 1)];
            case LOOP -> animation.loopFrames[frameIndex % animation.loopFrames.length];
            case FINISH -> animation.finishFrames[Math.min(frameIndex, animation.finishFrames.length - 1)];
        };
    }

	@Override
	public double getDrawnHeight() {
		if(getCurrentSprite() == null) return 0;
		return getCurrentSprite().getSprite().getHeight();
	}
	

	@Override
	public void applyEffect(LivingEntity entity) {
		double coolDown = 1/getDefinition().getEffectRate();
		double closestInfCoolDownMultiple = Math.floor(getAge()/coolDown) * coolDown;
		
		if(getLastAge() <= closestInfCoolDownMultiple && getAge() >= closestInfCoolDownMultiple) {
	    	WeaponItemDefinition definition = (WeaponItemDefinition) getWeapon().getWeaponItem().getItemDefinition();
			if(!entity.isInvincible()) {
				entity.setHp(Math.max(0, entity.getHp() - definition.getDamage()));
				entity.onHit();
			}
		}
		
	}

	@Override
	public void onDesactivate() {}

}
