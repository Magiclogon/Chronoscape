package ma.ac.emi.gamelogic.weapon;

import java.awt.*;
import java.awt.geom.AffineTransform;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.attack.manager.AttackObjectManager;
import ma.ac.emi.gamelogic.attack.type.AOEDefinition;
import ma.ac.emi.gamelogic.attack.type.ProjectileDefinition;
import ma.ac.emi.gamelogic.attack.type.ProjectileFactory;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.WeaponItem;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.input.MouseHandler;
import ma.ac.emi.math.Vector2D;

@Getter
@Setter
@EqualsAndHashCode
public class Weapon {
	protected WeaponItem weaponItem;
	
    protected Vector2D pos;
    protected Vector2D dir;
    protected LivingEntity bearer;
    protected double tsla;
    protected int ammo;
    protected double tssr;
    protected AttackObjectManager attackObjectManager;
    
    protected AttackStrategy attackStrategy;
        
    public Weapon(WeaponItem weaponItem) {
    	this.weaponItem = weaponItem;
        pos = new Vector2D();
        dir = new Vector2D();
        tsla = 0;
        tssr = 0;
        
        if(weaponItem != null) {
        	WeaponItemDefinition definition = (WeaponItemDefinition) weaponItem.getItemDefinition();
        	attackStrategy = WeaponStrategies.STRATEGIES.get(definition.getAttackStrategy());
            setAmmo(definition.getMagazineSize());
        }
    }
    
    public void attack() {
        if (getAttackStrategy() != null) {
            getAttackStrategy().execute(this);
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
        setTsla(getTsla() + step);
        
        if (getAmmo() == 0) {
            setTssr(getTssr() + step);
        }
        
        if (tssr >= ((WeaponItemDefinition)weaponItem.getItemDefinition()).getReloadingTime()) {
            setAmmo(((WeaponItemDefinition)weaponItem.getItemDefinition()).getMagazineSize());
            setTssr(0);
        }
    }
    
    public void snapTo(LivingEntity entity) {
        setBearer(entity);
    }

    public void reload() {
        if (ammo == ((WeaponItemDefinition)weaponItem.getItemDefinition()).getMagazineSize() || tssr > 0) return;
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