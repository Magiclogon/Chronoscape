package ma.ac.emi.gamelogic.weapon;

import java.awt.*;
import java.awt.geom.AffineTransform;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.attack.manager.AttackObjectManager;
import ma.ac.emi.gamelogic.entity.Entity;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.WeaponItem;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.input.MouseHandler;
import ma.ac.emi.math.Vector3D;

@Getter
@Setter
public class Weapon extends Entity{
	protected WeaponItem weaponItem;
	
    private Vector3D dir;
    private LivingEntity bearer;
    private double tsla;
    private int ammo;
    private double tssr;
    private AttackObjectManager attackObjectManager;
    
    protected AttackStrategy attackStrategy;
        
    public Weapon(WeaponItem weaponItem) {
    	this.weaponItem = weaponItem;
        pos = new Vector3D();
        dir = new Vector3D();
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
        double theta = getDir()!=null? Math.atan2(getDir().getY(), getDir().getX()) : 0;
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
        
        setDir(getBearer().getDir());
    }
    
    public void snapTo(LivingEntity entity) {
        setBearer(entity);
    }

    public void reload() {
        if (ammo == ((WeaponItemDefinition)weaponItem.getItemDefinition()).getMagazineSize() || tssr > 0) return;
        System.out.println("Reloading weapon...");
        tssr = 0;
    }
    
    public boolean isFromPlayer() {
        return getBearer() instanceof Player;
    }

	@Override
	public void initStateMachine() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setupAnimations() {
		// TODO Auto-generated method stub
		
	}
}