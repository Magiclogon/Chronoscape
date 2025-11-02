package ma.ac.emi.gamelogic.attack.type;

import ma.ac.emi.gamelogic.attack.Projectile;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.math.Vector2D;

public interface ProjectileFactory {
	Projectile createProjectile(String id, Vector2D pos, Vector2D dir, Weapon weapon);
       
}
