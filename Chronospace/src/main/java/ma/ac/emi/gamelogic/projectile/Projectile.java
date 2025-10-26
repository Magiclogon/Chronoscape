package ma.ac.emi.gamelogic.projectile;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.entity.Entity;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.math.Vector2D;
import ma.ac.emi.world.World;

@Getter
@Setter
public class Projectile extends Entity{
	private ProjectileType projectileType;
	
    private Vector2D pos;
    private Vector2D velocity;
    private double radius = 2;
    private boolean active;
    private boolean fromPlayer;
    
    
    private Weapon weapon;

    public Projectile(Vector2D pos, Vector2D dir, ProjectileType projectileType, Weapon weapon, boolean fromPlayer) {
    	super(pos, projectileType.getBaseSpeed());
    	this.projectileType = projectileType;
    	
        this.pos = pos;
        this.velocity = dir.mult(projectileType.getBaseSpeed());
        this.active = true;
        this.bound = new Rectangle(projectileType.getBoundWidth(), projectileType.getBoundHeight());
        this.weapon = weapon;
        this.fromPlayer = fromPlayer;
    }

	public void update(double step, World world) {
        setPos(getPos().add(velocity.mult(step)));
        
        bound.x = (int) getPos().getX();
        bound.y = (int) getPos().getY();
        
        if(isOutOfWorld(world) || isOutOfRange()) {
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
            g2d.draw(bound);
    	}
        
    }
    
    public boolean isOutOfWorld(World world) {
    	return !(world.getBound().contains(this.getBound()));
    }
    
    public boolean isOutOfRange() {
    	return getPos().sub(getWeapon().getPos()).norm() > getWeapon().getRange();
    }

	@Override
	public void update(double step) {
		// TODO Auto-generated method stub
		
	}
}
