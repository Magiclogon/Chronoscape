package ma.ac.emi.gamelogic.weapon;

import java.awt.*;
import java.awt.geom.AffineTransform;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.attack.manager.AttackObjectManager;
import ma.ac.emi.gamelogic.attack.type.AOEType;
import ma.ac.emi.gamelogic.attack.type.ProjectileType;
import ma.ac.emi.gamelogic.entity.Entity;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.input.MouseHandler;
import ma.ac.emi.math.Vector2D;

@Getter
@Setter
public class Weapon {
    protected double damage;
    protected double range;
    protected double attackSpeed;
    protected Vector2D pos;
    protected Vector2D dir;
    protected Entity bearer;
    protected double tsla;
    protected int ammo;
    protected int magazineSize;
    protected double tssr;
    protected double reloadingTime;
    protected ProjectileType projectileType;
    protected AOEType aoeType;
    protected AttackObjectManager attackObjectManager;
    
    // Strategy pattern
    protected AttackStrategy attackStrategy;
    
    public Weapon() {
        pos = new Vector2D();
        dir = new Vector2D();
        tsla = 0;
    }
    
    public void attack() {
        if (attackStrategy != null) {
            attackStrategy.execute(this);
        }
    }
    
    // All existing methods remain the same
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
    
    public void update(double step) {
        setPos(getBearer().getPos());
        pointAt(MouseHandler.getInstance().getMouseWorldPos());
        setTsla(getTsla() + step);
        
        if (getAmmo() == 0) {
            setTssr(getTssr() + step);
        }
        
        if (tssr >= reloadingTime) {
            setAmmo(getMagazineSize());
            setTssr(0);
        }
    }
    
    public void snapTo(Entity entity) {
        setBearer(entity);
    }

    public void reload() {
        if (ammo == magazineSize || tssr > 0) return;
        System.out.println("Reloading weapon...");
        tssr = 0;
    }

    public void pointAt(Vector2D target) {
        setDir(target.sub(getPos()).normalize());
    }
    
    public boolean isFromPlayer() {
        return getBearer() instanceof Player;
    }
}