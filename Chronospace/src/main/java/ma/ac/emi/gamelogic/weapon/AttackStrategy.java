package ma.ac.emi.gamelogic.weapon;

import ma.ac.emi.camera.CameraShakeDefinition;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.math.Vector3D;

public abstract class AttackStrategy {
	protected CameraShakeDefinition cameraShakeDefinition;
	
	public AttackStrategy(CameraShakeDefinition cameraShakeDefinition) {
		this.cameraShakeDefinition = cameraShakeDefinition;
	}
    public void execute(Weapon weapon, Vector3D target, double step) {

		WeaponItemDefinition definition = ((WeaponItemDefinition) weapon.getWeaponItem().getItemDefinition());

		double recoil = definition.getRecoilForce();
		if(recoil != 0 && weapon.getBearer() != null) {
			Vector3D shootDir = weapon.getDir();
			Vector3D recoilDir = shootDir.normalize().mult(-1);
			Vector3D recoilVector = recoilDir.mult(recoil);

			weapon.getBearer().applyKnockback(recoilVector);
		}
	}
}