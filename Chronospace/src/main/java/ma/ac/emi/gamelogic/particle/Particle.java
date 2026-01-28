package ma.ac.emi.gamelogic.particle;

import java.awt.Graphics;
import java.awt.Graphics2D;

import com.jogamp.opengl.GL3;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.fx.Sprite;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamecontrol.GameObject;
import ma.ac.emi.glgraphics.GLGraphics;
import ma.ac.emi.glgraphics.Texture;
import ma.ac.emi.math.Vector3D;

@Getter
@Setter
public class Particle extends GameObject {
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
        
        baseColorCorrection = def.getColorCorrection();
        setLightingStrategy(def.getLightingStrategy());
        
        setLight(getLightingStrategy().getLight());
        if(getLight() != null) {
            getLight().x = (float)pos.getX();
            getLight().y = (float)pos.getY();
            getLight().radius = Math.max(def.getAnimationDetails().spriteWidth, 
                                        def.getAnimationDetails().spriteHeight);
        }
        
        GameController.getInstance().getGameGLPanel().getRenderer().removeDrawable(this);
    }
    
    public void update(double step) {
        super.update(step);
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
        
        // Update light position if present
        if (getLight() != null) {
            getLight().x = (float)pos.getX();
            getLight().y = (float)pos.getY();
        }
    }
    
    public void draw(Graphics g) {
        Sprite sprite = animation.getSprite(phase, frameIndex);
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
    public double getDrawnHeight() {
        Sprite sprite = animation.getSprite(phase, frameIndex);
        return sprite != null ? sprite.getSprite().getHeight() : 0;
    }
    
    @Override
    public void drawGL(GL3 gl, GLGraphics glGraphics) {
        // Get pre-cached texture directly
        Texture texture = animation.getTexture(phase, frameIndex);
        Sprite sprite = animation.getSprite(phase, frameIndex);
        
        if (texture == null || sprite == null) return;
        
        glGraphics.drawSprite(gl, texture, 
            (float)(getPos().getX() - sprite.getWidth() / 2), 
            (float)(getPos().getY() - sprite.getHeight() / 2),
            sprite.getWidth(),
            sprite.getHeight(),
            getLightingStrategy(),
            getColorCorrection()
        );
    }
    
    /**
     * Batched rendering - texture already bound, skip binding
     */
    public void drawGLBatched(GL3 gl, GLGraphics glGraphics, Texture texture) {
        Sprite sprite = animation.getSprite(phase, frameIndex);
        if (sprite == null) return;
        
        // Use a version of drawSprite that doesn't bind texture
        glGraphics.drawSpriteBatched(gl, texture,
            (float)(getPos().getX() - sprite.getWidth() / 2), 
            (float)(getPos().getY() - sprite.getHeight() / 2),
            sprite.getWidth(),
            sprite.getHeight(),
            getLightingStrategy(),
            getColorCorrection()
        );
    }
}