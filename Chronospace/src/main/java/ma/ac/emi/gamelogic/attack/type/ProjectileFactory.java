package ma.ac.emi.gamelogic.attack.type;

import java.util.List;

import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.attack.Projectile;
import ma.ac.emi.gamelogic.attack.behavior.Behavior;
import ma.ac.emi.gamelogic.attack.manager.AttackObjectManager;
import ma.ac.emi.gamelogic.physics.AABB;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.math.Vector3D;

public class ProjectileFactory {
	public static Projectile createProjectile(String id, Vector3D pos, Vector3D dir, double speed,
			Weapon weapon, Vector3D target, AttackObjectManager manager) {
		return createProjectile(id, pos, dir, speed, weapon, target, List.of(), manager);
	}
	
	public static Projectile createProjectile(String id, Vector3D pos, Vector3D dir, double speed,
			Weapon weapon, Vector3D target, List<Behavior> additionalBehaviors, AttackObjectManager manager) {
		ProjectileDefinition def = ProjectileLoader.getInstance().get(id);
		if (def == null) {
			throw new IllegalArgumentException("Unknown projectile id: " + id);
		}
		
		Projectile projectile = manager.getPool(Projectile.class).obtain();
		
		projectile.reset(pos, dir, speed, def.getBoundWidth(), def.getBoundHeight(), weapon, target);
		
		def.getBehaviorDefinitions().forEach(b -> {
			projectile.addBehavior(b.create());
		});
		projectile.getBehaviors().addAll(additionalBehaviors);
		
		projectile.setSprite(AssetsLoader.getSprite(def.getSpritePath()));
		
		projectile.setBaseColorCorrection(def.getColorCorrection());
		projectile.setLightingStrategy(def.getLightingStrategy());
		projectile.init();
		
		projectile.activate();
		
		GameController.getInstance().addDrawable(projectile);
		return projectile;
	}
       
}
