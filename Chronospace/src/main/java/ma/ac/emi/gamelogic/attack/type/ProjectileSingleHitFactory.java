package ma.ac.emi.gamelogic.attack.type;

import ma.ac.emi.gamelogic.attack.Projectile;
import ma.ac.emi.gamelogic.attack.ProjectileSingleHit;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.math.Vector2D;

public class ProjectileSingleHitFactory implements ProjectileFactory{
	private static ProjectileSingleHitFactory instance;
	private ProjectileSingleHitFactory() {}
	
	public static ProjectileSingleHitFactory getInstance() {
		if(instance == null) instance = new ProjectileSingleHitFactory();
		return instance;
	}
	
	@Override
	public Projectile createProjectile(String id, Vector2D pos, Vector2D dir, Weapon weapon) {
		ProjectileSingleHitDefinition def = (ProjectileSingleHitDefinition) ProjectileLoader.getInstance().get(id);
		if (def == null) {
	        throw new IllegalArgumentException("Unknown projectile id: " + id);
	    }
	
	    return new ProjectileSingleHit(pos, dir, def, weapon);
	}
	

}
