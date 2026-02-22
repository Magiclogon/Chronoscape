package ma.ac.emi.gamelogic.weapon;

import ma.ac.emi.camera.CameraShakeDefinition;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.math.Vector3D;
import ma.ac.emi.sound.SoundManager;

public abstract class AttackStrategy {
	protected CameraShakeDefinition cameraShakeDefinition;
	
	public AttackStrategy(CameraShakeDefinition cameraShakeDefinition) {
		this.cameraShakeDefinition = cameraShakeDefinition;
	}
    public void execute(Weapon weapon, Vector3D target, double step) {

		WeaponItemDefinition definition = ((WeaponItemDefinition) weapon.getWeaponItem().getItemDefinition());

		String attackSound = definition.getAttackSound();
		if (attackSound != null && !attackSound.isEmpty()) {
			SoundManager soundManager = GameController.getInstance().getSoundManager();
			if (soundManager != null) {
				double attackSpeed = definition.getAttackSpeed();
				double timeBetweenAttacks = 1.0 / attackSpeed;
				double soundDuration = soundManager.getDuration(attackSound);

				float speedFactor = 1.0f;
				if (soundDuration > timeBetweenAttacks && timeBetweenAttacks > 0) {
					speedFactor = (float) (soundDuration / timeBetweenAttacks);
				}

				soundManager.play(attackSound, true, speedFactor);
			}
		}

		double recoil = definition.getRecoilForce();
		if(recoil != 0 && weapon.getBearer() != null) {
			Vector3D shootDir = weapon.getDir();
			Vector3D recoilDir = shootDir.normalize().mult(-1);
			Vector3D recoilVector = recoilDir.mult(recoil);

			weapon.getBearer().applyKnockback(recoilVector);
		}
		weapon.setAttacking(false);

	}
}