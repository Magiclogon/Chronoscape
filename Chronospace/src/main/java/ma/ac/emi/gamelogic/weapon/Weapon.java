package ma.ac.emi.gamelogic.weapon;

import java.awt.*;
import java.awt.geom.AffineTransform;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.attack.manager.AttackObjectManager;
import ma.ac.emi.gamelogic.attack.type.AOEType;
import ma.ac.emi.gamelogic.entity.Entity;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.input.MouseHandler;
import ma.ac.emi.math.Vector2D;

@Getter
@Setter
public abstract class Weapon {
    protected double damage;
    protected double range;
    protected double attackSpeed;
    protected Vector2D pos;
    protected Vector2D dir;
	protected Entity bearer;
	protected double tsla; //Time Since Last Attack
	protected int ammo;
	protected int magazineSize;
	protected double tssr; //Time Since Start Reloading
	protected double reloadingTime;
	
	protected AOEType aoeType;
	protected AttackObjectManager attackObjectManager;


	    
    public Weapon() {
    	pos = new Vector2D();
    	dir = new Vector2D();
    	tsla = 0;
    }
    
    public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform oldTransform = g2d.getTransform();

		double theta = Math.atan2(getDir().getY(), getDir().getX());

		g2d.translate(getPos().getX(), getPos().getY());
		g2d.rotate(theta);

		g2d.setColor(Color.GRAY);
		g2d.fillRect(0, 0, 16, 8);

		g2d.setTransform(oldTransform);
	}

    public abstract void attack();
    
    public void update(double step) {
		setPos(getBearer().getPos());
		pointAt(MouseHandler.getInstance().getMouseWorldPos());
		setTsla(getTsla() + step);
		
		if(getAmmo() == 0) {
			setTssr(getTssr() + step);
		}
		
		if(tssr >= reloadingTime) {
			setAmmo(getMagazineSize());
			setTssr(0);
		}
		
		System.out.println(getAmmo());
	}
    
    public void snapTo(Entity entity) {
		setBearer(entity);
		
	}
    
    public void pointAt(Vector2D target) {
    	setDir(target.sub(getPos()).normalize());
    }
    
    public boolean isFromPlayer() {
    	return getBearer() instanceof Player;
    }
    
    @Override
	public String toString() {
		return this.getClass().getName();
	}

}
