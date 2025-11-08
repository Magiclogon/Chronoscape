package ma.ac.emi.gamelogic.attack.type;

import ma.ac.emi.gamelogic.attack.Projectile;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.math.Vector3D;

public interface ProjectileFactory {
	Projectile createProjectile(String id, Vector3D pos, Vector3D dir, Weapon weapon);
       
}
