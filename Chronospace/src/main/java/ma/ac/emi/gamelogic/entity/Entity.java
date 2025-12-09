package ma.ac.emi.gamelogic.entity;

import java.awt.*;
import java.awt.image.BufferedImage;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.fx.StateMachine;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamecontrol.GameObject;
import ma.ac.emi.math.Vector3D;

@Setter
@Getter
public abstract class Entity extends GameObject{
	protected Vector3D velocity;
	protected StateMachine stateMachine;
	
	public Entity() {
		stateMachine = new StateMachine();
		initStateMachine();
		setupAnimations();
	}
	
	@Override
	public void draw(Graphics g) {
        if(stateMachine.getCurrentAnimationState() != null) {
        	BufferedImage sprite = stateMachine.getCurrentAnimationState().getCurrentFrameSprite().getSprite();
        	g.drawImage(sprite, (int)(pos.getX()-sprite.getWidth()/2), (int)(pos.getY()-sprite.getHeight()/2), null);
        }
        else
            g.drawImage(AssetsLoader.getSprite("default_sprite.png").getSprite(), (int)(pos.getX()-hitbox.width/2), (int)(pos.getY()-hitbox.height/2), null);

        g.setColor(Color.yellow);
        g.drawRect(hitbox.x-hitbox.width/2, hitbox.y-hitbox.height/2, hitbox.width, hitbox.height);
        //g.drawRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        
        g.setColor(Color.red);
        g.drawRect(bound.x-bound.width/2, bound.y-bound.height/2, bound.width, bound.height);

	}
	
	
	public abstract void initStateMachine();
	public abstract void setupAnimations();
	
}
