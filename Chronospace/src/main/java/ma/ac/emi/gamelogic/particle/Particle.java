package ma.ac.emi.gamelogic.particle;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.fx.Sprite;
import ma.ac.emi.gamecontrol.GameObject;
import ma.ac.emi.math.Vector3D;

@Getter
@Setter
public class Particle extends GameObject{

    private double age;
    private double frameTimer;

    private int frameIndex;
    private ParticlePhase phase;

    private final ParticleDefinition definition;
    private final ParticleAnimation animation;

    public boolean alive = true;

    public Particle(ParticleDefinition def, Vector3D pos) {
        this.definition = def;
        this.pos = new Vector3D(pos);

        this.animation = ParticleAnimationCache.get(def);
        this.phase = ParticlePhase.INIT;
        
        
    }

    public void update(double step) {
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
                    phase = ParticlePhase.LOOP;
                }
            }
            case LOOP -> {
                if (age >= definition.getLifetime()) {
                    frameIndex = 0;
                    phase = ParticlePhase.FINISH;
                } else {
                    frameIndex %= animation.loopFrames.length;
                }
            }
            case FINISH -> {
                if (frameIndex >= animation.finishFrames.length) {
                    alive = false;
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

}
