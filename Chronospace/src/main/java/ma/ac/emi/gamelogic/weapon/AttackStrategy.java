package ma.ac.emi.gamelogic.weapon;

import ma.ac.emi.camera.CameraShakeDefinition;
import ma.ac.emi.math.Vector3D;

public abstract class AttackStrategy {
	protected CameraShakeDefinition cameraShakeDefinition;
	
	public AttackStrategy(CameraShakeDefinition cameraShakeDefinition) {
		this.cameraShakeDefinition = cameraShakeDefinition;
	}
    public abstract void execute(Weapon weapon, Vector3D target, double step);
}