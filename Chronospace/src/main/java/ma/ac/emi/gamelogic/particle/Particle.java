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
import ma.ac.emi.math.Matrix4;
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
    
    public Vector3D dir;
    public GameObject source;
    
    public Particle(ParticleDefinition def, Vector3D pos, Vector3D dir, GameObject source) {
        this.definition = def;
        this.pos = new Vector3D(pos);
        this.dir = new Vector3D(def.getDirX(), def.getDirY());
        if(!def.isHasFixedDir()) this.dir = new Vector3D(dir);
        
        this.animation = ParticleAnimationCache.get(def);
        this.phase = ParticlePhase.INIT;
        
        baseColorCorrection = def.getColorCorrection();
        setLightingStrategy(def.getLightingStrategy());
        
        this.source = source;
        
        GameController.getInstance().removeDrawable(this);
        
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
            getLight().setPosition(pos);
        }
        
        if(source != null) {
        	if(definition.isShouldFollowSource()) setPos(source.getPos());
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
        Sprite sprite = getCurrentSprite();
        
        if (texture == null || sprite == null) return;
        
        float[] model = new float[16];
        Matrix4.identity(model);

        float px = (float) getPos().getX();
        float py = (float) (getPos().getY() - getPos().getZ());
        Matrix4.translate(model, px, py, 0f);

        double theta = getDir() != null ? getDir().getAngle() : 0;
        Matrix4.rotateZ(model, (float) theta);

        float wx = -sprite.getWidth()/2;
        float wy = -sprite.getHeight() / 2f;
        Matrix4.translate(model, wx, wy, 0f);

        Matrix4.scale(model, sprite.getWidth(), sprite.getHeight(), 1f);
        
        glGraphics.drawSprite(gl, texture, 
            model,
            getLightingStrategy(),
            getColorCorrection()
        );
//        glGraphics.drawSprite(gl, texture, 
//        		(float)(getPos().getX() - sprite.getWidth() / 2), 
//        		(float)(getPos().getY() - sprite.getHeight() / 2 - pos.getZ()),
//        		sprite.getWidth(),
//        		sprite.getHeight(),
//        		getLightingStrategy(),
//        		getColorCorrection()
//        		);
        
    }
    
    @Override
    public Sprite getCurrentSprite() {
    	return animation.getSprite(phase, frameIndex);
    }
    
    public void drawGLBatched(GL3 gl, GLGraphics glGraphics, Texture texture) {
        Sprite sprite = animation.getSprite(phase, frameIndex);
        if (sprite == null) return;
        
        // Use a version of drawSprite that doesn't bind texture
        float[] model = new float[16];
        Matrix4.identity(model);

        float px = (float) getPos().getX();
        float py = (float) (getPos().getY() - getPos().getZ());
        Matrix4.translate(model, px, py, 0f);

        double theta = getDir() != null ? getDir().getAngle() : 0;
        Matrix4.rotateZ(model, (float) theta);

        float wx = -sprite.getWidth()/2;
        float wy = -sprite.getHeight() / 2f;
        Matrix4.translate(model, wx, wy, 0f);

        Matrix4.scale(model, sprite.getWidth(), sprite.getHeight(), 1f);
        
        glGraphics.drawSpriteBatched(gl, texture,
            model,
            getLightingStrategy(),
            getColorCorrection()
        );
    }
}