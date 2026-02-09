package ma.ac.emi.gamelogic.entity;

import java.awt.*;
import java.awt.image.BufferedImage;

import com.jogamp.opengl.GL3;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.fx.Sprite;
import ma.ac.emi.fx.StateMachine;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamecontrol.GameObject;
import ma.ac.emi.glgraphics.GLGraphics;
import ma.ac.emi.glgraphics.Texture;
import ma.ac.emi.glgraphics.color.SpriteColorCorrection;
import ma.ac.emi.math.Vector3D;

@Setter
@Getter
public abstract class Entity extends GameObject{
	protected Vector3D velocity;
	protected Vector3D knockback;
	protected double friction = 0.9;
	protected StateMachine stateMachine;
	
	public Entity() {
		stateMachine = new StateMachine();
		initStateMachine();

		baseColorCorrection = new SpriteColorCorrection();
		baseColorCorrection.setValue(0.5f);
		this.knockback = new Vector3D(0, 0);
	}

	public void applyKnockback(Vector3D force) {
		this.knockback = this.knockback.add(force);
	}
	
	@Override
	public void update(double step) {
		super.update(step);
		if(getVelocity() != null) setPos(getPos().add(getVelocity().mult(step)));


		if (Math.abs(knockback.getX()) > 0.1 || Math.abs(knockback.getY()) > 0.1) {
			setPos(getPos().add(knockback.mult(step)));
			knockback = knockback.mult(friction);
		} else {
			knockback.init();
		}
	}
	
	@Override
	public void draw(Graphics g) {
        if(stateMachine.getCurrentAnimationState() != null) {
        	BufferedImage sprite = stateMachine.getCurrentAnimationState().getCurrentFrameSprite().getSprite();
        	g.drawImage(sprite, (int)(getPos().getX()-sprite.getWidth()/2), (int)(getPos().getY()-sprite.getHeight()/2), null);
        }
        else
            //g.drawImage(AssetsLoader.getSprite("default_sprite.png").getSprite(), (int)(pos.getX()-hitbox.width/2), (int)(pos.getY()-hitbox.height/2), null);

        g.setColor(Color.yellow);
        //g.drawRect(hitbox.x-hitbox.width/2, hitbox.y-hitbox.height/2, hitbox.width, hitbox.height);

	}
	
	@Override
	public void drawGL(GL3 gl, GLGraphics glGraphics) {

	    Sprite sprite = stateMachine.getCurrentAnimationState() != null
	            ? stateMachine.getCurrentAnimationState().getCurrentFrameSprite()
	            : AssetsLoader.getSprite("default_sprite.png");

	    Texture texture = sprite.getTexture(gl);

	    glGraphics.drawSprite(
	            gl,
	            texture,
	            (float)(getPos().getX() - sprite.getWidth() / 2f),
	            (float)(getPos().getY() - sprite.getHeight() / 2f),
	            sprite.getWidth(),
	            sprite.getHeight(),
	            getColorCorrection()
	    );
	    
	}

	
	public abstract void initStateMachine();
	public abstract void setupAnimations();

	
}
