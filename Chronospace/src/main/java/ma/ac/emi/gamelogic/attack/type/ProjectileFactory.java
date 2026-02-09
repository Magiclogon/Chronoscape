package ma.ac.emi.gamelogic.attack.type;

import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.gamelogic.attack.Projectile;
import ma.ac.emi.gamelogic.physics.AABB;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.math.Vector3D;

public class ProjectileFactory {
	public static Projectile createProjectile(String id, Vector3D pos, Vector3D dir, double speed, Weapon weapon, Vector3D target) {
		ProjectileDefinition def = ProjectileLoader.getInstance().get(id);
		if (def == null) {
	        throw new IllegalArgumentException("Unknown projectile id: " + id);
	    }
		
		Projectile projectile = new Projectile(pos, dir, weapon, target);
    	
        projectile.setVelocity(dir.mult(speed));
        projectile.setHitbox(new AABB(pos, new Vector3D(def.getBoundWidth(), def.getBoundHeight())));
        
        def.getBehaviorDefinitions().forEach(b -> {
        	projectile.addBehavior(b.create());
        });
        projectile.setSprite(AssetsLoader.getSprite(def.getSpritePath()));
        
        projectile.setBaseColorCorrection(def.getColorCorrection());
        projectile.setLightingStrategy(def.getLightingStrategy());
        projectile.init();
        
	    return projectile;
	}
       
}
