package ma.ac.emi.gamelogic.weapon;

import ma.ac.emi.camera.CameraShakeDefinition;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.math.Vector3D;
import ma.ac.emi.sound.WeaponSoundController;

public abstract class AttackStrategy {

	protected CameraShakeDefinition cameraShakeDefinition;

	public AttackStrategy(CameraShakeDefinition cameraShakeDefinition) {
		this.cameraShakeDefinition = cameraShakeDefinition;
	}

	public void execute(Weapon weapon, Vector3D target, double step) {

		WeaponItemDefinition definition =
				(WeaponItemDefinition) weapon.getWeaponItem().getItemDefinition();

		// ── Sound ─────────────────────────────────────────────────────────────
		WeaponSoundController.playAttackSound(
				definition.getId(),
				definition.getAttackSound(),
				definition.getAttackSpeed()
		);

		// ── Recoil ────────────────────────────────────────────────────────────
		double recoil = definition.getRecoilForce();
		if (recoil != 0 && weapon.getBearer() != null) {
			Vector3D recoilDir    = weapon.getDir().normalize().mult(-1);
			Vector3D recoilVector = recoilDir.mult(recoil);
			weapon.getBearer().applyKnockback(recoilVector);
		}

		weapon.setAttacking(false);
	}

	/**
	 * Called by the weapon when it is switched out or the player stops firing.
	 * Override in subclasses that need additional cleanup (e.g. MeleeAttackStrategy).
	 */
	public void onStopFiring(Weapon weapon) {
		WeaponItemDefinition definition =
				(WeaponItemDefinition) weapon.getWeaponItem().getItemDefinition();

		WeaponSoundController.stopLoopedAttackSound(
				definition.getId(),
				definition.getAttackSound()
		);
	}
}