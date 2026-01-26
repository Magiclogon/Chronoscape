package ma.ac.emi.gamelogic.attack;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.fx.Sprite;
import ma.ac.emi.gamelogic.attack.behavior.Behavior;
import ma.ac.emi.gamelogic.attack.type.ProjectileDefinition;
import ma.ac.emi.gamelogic.entity.Entity;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.math.Vector3D;
import ma.ac.emi.world.World;

@Getter
@Setter
public class Projectile extends AttackObject{
	private Vector3D startingPos;
	private List<Behavior> behaviors = new ArrayList<>();
    private double radius = 2;
    private Sprite sprite;
    
    public Projectile(Vector3D pos, Vector3D dir, Weapon weapon) {
    	super(pos, weapon);
    	this.startingPos = pos;
    }
    
    public void init() {
    	behaviors.forEach(b -> b.onInit(this));
    }

	public void update(double step) {
        super.update(step);
        
        hitbox.x = (int) getPos().getX();
        hitbox.y = (int) getPos().getY();
        
        if(isOutOfRange()) {
        	setActive(false);
        }
        
        behaviors.forEach(b -> b.onUpdate(this, step));
    }

    public void draw(Graphics g) {
    	Graphics2D g2d = (Graphics2D) g;
    	if(isActive()) {
    		if(sprite != null) {
    			AffineTransform oldTransform = g2d.getTransform();
    			g2d.translate(pos.getX(), pos.getY());
    			g2d.rotate(velocity.getAngle());
    			g2d.drawImage(sprite.getSprite(), -sprite.getSprite().getWidth()/2, -sprite.getSprite().getHeight()/2, null);
    			g2d.setTransform(oldTransform);
    			
    		}else {
	    		g2d.setColor(Color.red);
	            g2d.fillOval((int)(pos.getX() - radius), (int)(pos.getY() - radius),
	                         (int)(radius * 2), (int)(radius * 2));
	            g2d.setColor(Color.black);
	            g2d.drawRect(hitbox.x-hitbox.width/2, hitbox.y-hitbox.height/2, hitbox.width, hitbox.height);
    		}
    	}
        
    }
    
    public boolean isOutOfRange() {
    	WeaponItemDefinition definition = (WeaponItemDefinition) getWeapon().getWeaponItem().getItemDefinition();
    	return getPos().sub(getStartingPos()).norm() > definition.getRange();
    }

	@Override
	public void applyEffect(LivingEntity entity) {
		System.out.println("applying effect");
		behaviors.forEach(b -> b.onHit(this, entity));
		setActive(false);
	}
	
	@Override
	public void onDesactivate() {
		behaviors.forEach(b -> b.onDesactivate(this));
	}
	
	public void addBehavior(Behavior behavior) {
		behaviors.add(behavior);
	}
	
	public void removeBehavior(Behavior behavior) {
		behaviors.remove(behavior);
	}
	
	@Override
	public double getDrawnHeight() {
		return radius*2;
	}

}
