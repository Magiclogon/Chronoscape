package ma.ac.emi.gamelogic.attack.type;

import ma.ac.emi.gamelogic.attack.Projectile;
import ma.ac.emi.gamelogic.attack.ProjectileAOE;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.math.Vector3D;

public class ProjectileAOEFactory implements ProjectileFactory{
	private static ProjectileAOEFactory instance;
	private ProjectileAOEFactory() {}
	
	public static ProjectileAOEFactory getInstance() {
		if(instance == null) instance = new ProjectileAOEFactory();
		return instance;
	}

	@Override
	public Projectile createProjectile(String id, Vector3D pos, Vector3D dir, Weapon weapon) {
		ProjectileAOEDefinition def = (ProjectileAOEDefinition) ProjectileLoader.getInstance().get(id);
		if (def == null) {
	        throw new IllegalArgumentException("Unknown projectile id: " + id);
	    }
	
	    return new ProjectileAOE(pos, dir, def, weapon);
	}
}
