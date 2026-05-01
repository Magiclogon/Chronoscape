package ma.ac.emi.gamelogic.weapon;

import java.util.ArrayList;
import java.util.List;

import ma.ac.emi.camera.CameraShakeDefinition;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.attack.Projectile;
import ma.ac.emi.gamelogic.attack.behavior.LobbingBehavior;
import ma.ac.emi.gamelogic.attack.behavior.SoundProjectileBehavior;
import ma.ac.emi.gamelogic.attack.type.ProjectileFactory;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.math.Vector3D;

public class LobbedStrategy extends AttackStrategy{
	private double radius;
	private int projectileCount;
	private double gravity;
	private double scale;

	public LobbedStrategy(double radius, int projectileCount, double gravity, double scale, CameraShakeDefinition cameraShakeDefinition) {
		super(cameraShakeDefinition);
		this.radius = radius;
		this.projectileCount = projectileCount;
		this.gravity = gravity;
		this.scale = scale;
	}
	
	@Override
    public void execute(Weapon weapon, Vector3D target, double step) {
    	WeaponItemDefinition definition = ((WeaponItemDefinition) weapon.getWeaponItem().getItemDefinition());
    	double effectiveAttackSpeed = Math.max(weapon.caps().minAttackSpeed, Math.min(weapon.caps().maxAttackSpeed, definition.getAttackSpeed())) * weapon.getBearer().getAttackSpeedMultiplier();
    	if (weapon.getTsla() >= 1/effectiveAttackSpeed && weapon.getAmmo() > 0) {
        	for(int i = 0; i < projectileCount; i++) {        		
        		Vector3D newTarget = target.add(Vector3D.randomUnit2().mult(radius*Math.random()));
        		Vector3D dir = newTarget.sub(weapon.getPos()).normalize();
        		
        		if(newTarget.sub(weapon.getPos()).norm() > definition.getRange()) {
        			newTarget = weapon.getPos().add(dir.mult(definition.getRange()));
        		}
        		
        		List<ma.ac.emi.gamelogic.attack.behavior.ProjectileBehavior> additionalBehaviors = new ArrayList<>();
        		additionalBehaviors.add(new LobbingBehavior(gravity, scale));
        		if (definition.getDeactivateSound() != null && !definition.getDeactivateSound().isEmpty()) {
        			additionalBehaviors.add(new SoundProjectileBehavior(definition.getDeactivateSound()));
        		}
        		
        		Projectile projectile = ProjectileFactory.createProjectile(
            			definition.getProjectileId(),
            			weapon.getPos().add(weapon.getRelativeProjectilePos()),
            			dir,
            			definition.getProjectileSpeed()*weapon.getBearer().getProjectileSpeedMultiplier(),
            			weapon,
            			newTarget,
            			additionalBehaviors,
            			weapon.getAttackObjectManager()
            		);
        		
        		
                weapon.getAttackObjectManager().addObject(projectile);
        	}
            weapon.consumeAmmo();

			super.execute(weapon, target, step);

            weapon.setTsla(0);
            weapon.getStateMachine().getCurrentAnimationState().reset();
            
            weapon.getBehaviors().forEach(b -> b.onAttack(weapon, step));

            GameController.getInstance().getCamera().shake(cameraShakeDefinition.intensity, cameraShakeDefinition.damping);
        }
    }

}
