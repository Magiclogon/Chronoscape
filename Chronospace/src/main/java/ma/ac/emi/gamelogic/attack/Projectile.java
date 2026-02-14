package ma.ac.emi.gamelogic.attack;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL3;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.fx.Sprite;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.attack.behavior.Behavior;
import ma.ac.emi.gamelogic.attack.behavior.LobbingBehavior;
import ma.ac.emi.gamelogic.attack.type.ProjectileDefinition;
import ma.ac.emi.gamelogic.entity.Entity;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.physics.AABB;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.glgraphics.GLGraphics;
import ma.ac.emi.glgraphics.Texture;
import ma.ac.emi.glgraphics.color.SpriteColorCorrection;
import ma.ac.emi.glgraphics.lighting.NoLightingStrategy;
import ma.ac.emi.math.Matrix4;
import ma.ac.emi.math.Vector3D;
import ma.ac.emi.world.Obstacle;
import ma.ac.emi.world.World;

@Getter
@Setter
public class Projectile extends AttackObject{
	private Vector3D startingPos;
	private List<Behavior> behaviors = new ArrayList<>();
    private double radius = 2;
    private Sprite sprite;
    
    private Vector3D target;
    
    public Projectile() {setShouldGlow(true);}
    
    public void reset(Vector3D pos, Vector3D dir, double speed, 
    		double boundWidth, double boundHeight, Weapon weapon, Vector3D target) {
    	setPos(pos);
		setStartingPos(pos);
		setWeapon(weapon);
		setTarget(target);
		
		setVelocity(dir.mult(speed));
		setHitbox(new AABB(pos, new Vector3D(boundWidth, boundHeight)));
		
		setBaseColorCorrection(SpriteColorCorrection.NORMAL);
		setColorCorrection(SpriteColorCorrection.NORMAL);
		setLightingStrategy(new NoLightingStrategy());
		
		behaviors.clear();
    }
    
    public void init() {
    	behaviors.forEach(b -> b.onInit(this));
    }

	public void update(double step) {
        super.update(step);
        
        hitbox.center = pos;
        
        if(isOutOfRange() || pos.getZ() <= GameController.getInstance().getWorldZ()) {
            desactivate();
        }
        
        if(active) behaviors.forEach(b -> b.onUpdate(this, step));
        
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
	           // g2d.drawRect(hitbox.x-hitbox.width/2, hitbox.y-hitbox.height/2, hitbox.width, hitbox.height);
    		}
    	}
        
    }
    
    @Override
    public void drawGL(GL3 gl, GLGraphics glGraphics) {
		if(sprite != null) {
			if(shadow != null) shadow.drawGL(gl, glGraphics, this);
			
			Texture texture = sprite.getTexture(gl);
			
			float[] model = new float[16];
	        Matrix4.identity(model);

	        float px = (float) getPos().getX();
	        float py = (float) (getPos().getY() - getPos().getZ());
	        Matrix4.translate(model, px, py, 0f);

	        double theta = getVelocity() != null ? getVelocity().getAngle() : 0;
	        Matrix4.rotateZ(model, (float) theta);

	        float wx = -sprite.getWidth()/2;
	        float wy = -sprite.getHeight() / 2f;
	        Matrix4.translate(model, wx, wy, 0f);

	        Matrix4.scale(model, sprite.getWidth(), sprite.getHeight(), 1f);
	        
			glGraphics.drawSprite(gl, texture, model, getLightingStrategy(), getColorCorrection());
			
		}
//    		glGraphics.drawQuad(gl, (float)(hitbox.center.getX()-hitbox.half.getX()), (float)(hitbox.center.getY()-hitbox.half.getY()), 
//    				(float)(hitbox.half.getX()*2), (float)(hitbox.half.getY()*2));
	
    	
    }
    
    public boolean isOutOfRange() {
    	WeaponItemDefinition definition = (WeaponItemDefinition) getWeapon().getWeaponItem().getItemDefinition();
    	Vector3D diff = getPos().sub(getStartingPos());
    	Vector3D diffProj = new Vector3D(diff.getX(), diff.getY());
    	
    	return diffProj.norm() > definition.getRange();
    }

	@Override
	public void applyEffect(LivingEntity entity) {
		onHit(entity);
	}
	
	public void onHit(Obstacle obstacle) {
		behaviors.forEach(b -> b.onHit(this, obstacle));
		desactivate();
	}
	
	public void onHit(LivingEntity entity) {
		behaviors.forEach(b -> b.onHit(this, entity));
		desactivate();
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
	
	@Override
	public Sprite getCurrentSprite() {
		return sprite;
	}



}
