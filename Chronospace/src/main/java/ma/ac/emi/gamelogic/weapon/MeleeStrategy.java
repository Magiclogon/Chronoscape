package ma.ac.emi.gamelogic.weapon;

import ma.ac.emi.camera.CameraShakeDefinition;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.attack.Projectile;
import ma.ac.emi.gamelogic.attack.type.ProjectileFactory;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.math.Vector3D;

public class MeleeStrategy extends AttackStrategy {
	
	
    public MeleeStrategy(CameraShakeDefinition cameraShakeDefinition) {
		super(cameraShakeDefinition);
	}

	@Override
    public void execute(Weapon weapon, Vector3D target, double step) {
    	WeaponItemDefinition definition = ((WeaponItemDefinition) weapon.getWeaponItem().getItemDefinition());
        if (weapon.getTsla() >= 1/definition.getAttackSpeed()) {
        	Projectile projectile = ProjectileFactory.createProjectile(
        			definition.getProjectileId(),
        			weapon.getPos().add(weapon.getRelativeProjectilePos()),
        			weapon.getDir(),
        			definition.getProjectileSpeed(),
        			weapon,
        			target
        		);
            weapon.getAttackObjectManager().addObject(projectile);
            weapon.setTsla(0);
            weapon.getStateMachine().getCurrentAnimationState().reset();
            
            weapon.getBehaviors().forEach(b -> b.onAttack(weapon, step));
            GameController.getInstance().getCamera().shake(cameraShakeDefinition.intensity, cameraShakeDefinition.damping);
        }
    }
}