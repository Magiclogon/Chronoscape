package ma.ac.emi.gamelogic.weapon;

import java.awt.Graphics;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.entity.Entity;
import ma.ac.emi.input.MouseHandler;
import ma.ac.emi.math.Vector2D;

@Getter
@Setter
public abstract class Weapon {
    protected double damage;
    protected double range;
    protected double aoe;
    protected double attackSpeed;
    protected Vector2D pos;
    protected Vector2D dir;
	protected Entity bearer;

    
    public Weapon() {
    	pos = new Vector2D();
    	dir = new Vector2D();
    }
    
    public abstract void draw(Graphics g);
    public abstract void attack();
    
    public void update(double step) {
		setPos(getBearer().getPos());
		pointAt(MouseHandler.getInstance().getMouseWorldPos());
	}
    
    public void snapTo(Entity entity) {
		setBearer(entity);
		
	}
    
    public void pointAt(Vector2D target) {
    	setDir(target.sub(getPos()).normalize());
    }

}
