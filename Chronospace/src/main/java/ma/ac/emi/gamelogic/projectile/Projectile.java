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
    private Vector2D pos;
    private Vector2D velocity;
    private double radius = 2;
    private boolean active;
    private boolean fromPlayer;
    
    private Weapon weapon;

    public Projectile(Vector2D pos, Vector2D velocity, Rectangle bound, Weapon weapon, boolean fromPlayer) {
    	super(pos, velocity.norm());
        this.pos = pos;
        this.velocity = velocity;
        this.active = true;
        this.bound = bound;
        this.weapon = weapon;
        this.fromPlayer = fromPlayer;
    }

    public void update(double step, World world) {
    	System.out.println(bound);
        setPos(getPos().add(velocity.mult(step)));
        
        bound.x = (int) getPos().getX();
        bound.y = (int) getPos().getY();
        
        if(isOutOfWorld(world)) {
        	setActive(false);
        }
    }

    public void draw(Graphics g) {
    	Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.red);
        g2d.fillOval((int)(pos.getX() - radius), (int)(pos.getY() - radius),
                     (int)(radius * 2), (int)(radius * 2));
    }
    
    public boolean isOutOfWorld(World world) {
    	return !(world.getBound().contains(this.getBound()));
    }

	@Override
	public void update(double step) {
		// TODO Auto-generated method stub
		
	}
}
