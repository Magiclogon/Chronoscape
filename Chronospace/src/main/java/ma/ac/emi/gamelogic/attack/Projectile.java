package ma.ac.emi.gamelogic.attack;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.attack.type.ProjectileDefinition;
import ma.ac.emi.gamelogic.entity.Entity;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.math.Vector3D;
import ma.ac.emi.world.World;

@Getter
@Setter
public abstract class Projectile extends AttackObject{
	private ProjectileDefinition projectileType; 
	private Vector3D startingPos;
    private double radius = 2;
    
    public Projectile(Vector3D pos, Vector3D dir, ProjectileDefinition projectileType, Weapon weapon) {
    	super(pos, weapon);
    	this.projectileType = projectileType;
    	this.startingPos = pos;
    	
        this.velocity = dir.mult(projectileType.getBaseSpeed());
        this.hitbox = new Rectangle(projectileType.getBoundWidth(), projectileType.getBoundHeight());
    }

	public void update(double step) {
        super.update(step);
        
        hitbox.x = (int) getPos().getX();
        hitbox.y = (int) getPos().getY();
        
        if(isOutOfRange()) {
        	setActive(false);
        }
    }

    public void draw(Graphics g) {
    	Graphics2D g2d = (Graphics2D) g;
    	if(isActive()) {
    		g2d.setColor(Color.red);
            g2d.fillOval((int)(pos.getX() - radius), (int)(pos.getY() - radius),
                         (int)(radius * 2), (int)(radius * 2));
            g2d.setColor(Color.black);
            g2d.draw(hitbox);
    	}
        
    }
    
    public boolean isOutOfRange() {
    	WeaponItemDefinition definition = (WeaponItemDefinition) getWeapon().getWeaponItem().getItemDefinition();
    	return getPos().sub(getStartingPos()).norm() > definition.getRange();
    }

	@Override
	public void applyEffect(LivingEntity entity) {
		System.out.println("applying effect");
		setActive(false);
	}

}
