package ma.ac.emi.gamelogic.attack;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.fx.Sprite;
import ma.ac.emi.gamelogic.attack.type.AOEDefinition;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.particle.ParticleAnimation;
import ma.ac.emi.gamelogic.particle.ParticleAnimationCache;
import ma.ac.emi.gamelogic.particle.ParticleDefinition;
import ma.ac.emi.gamelogic.particle.ParticlePhase;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.math.Vector3D;

@Getter
@Setter
public class AOE extends AttackObject{
	private double age, lastAge;
    private double frameTimer;

    private int frameIndex;
    private AOEPhase phase;

    private final AOEDefinition definition;
    private final AOEAnimation animation;

    public AOE(AOEDefinition def, Vector3D pos, Weapon weapon) {
    	super(pos, weapon);
        this.definition = def;

        this.animation = AOEAnimationCache.get(def);
        this.phase = AOEPhase.INIT;
        
        this.hitbox = new Rectangle(def.getAnimationDetails().spriteWidth, def.getAnimationDetails().spriteHeight);
        this.pos.setZ(-1);
    }

    public void update(double step) {
    	hitbox.x = (int)getPos().getX();
		hitbox.y = (int)getPos().getY();
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

    private Sprite getCurrentSprite() {
        return switch (phase) {
            case INIT -> animation.initFrames[Math.min(frameIndex, animation.initFrames.length - 1)];
            case LOOP -> animation.loopFrames[frameIndex % animation.loopFrames.length];
            case FINISH -> animation.finishFrames[Math.min(frameIndex, animation.finishFrames.length - 1)];
        };
    }

	@Override
	public double getDrawnHeight() {
		return getCurrentSprite().getSprite().getHeight();
	}
	

	@Override
	public void applyEffect(LivingEntity entity) {
		double coolDown = 1/getDefinition().getEffectRate();
		double closestInfCoolDownMultiple = Math.floor(getAge()/coolDown) * coolDown;
		
		if(getLastAge() <= closestInfCoolDownMultiple && getAge() >= closestInfCoolDownMultiple) {
	    	WeaponItemDefinition definition = (WeaponItemDefinition) getWeapon().getWeaponItem().getItemDefinition();
			entity.setHp(Math.max(0, entity.getHp() - definition.getDamage()));
		}
		
	}

}
