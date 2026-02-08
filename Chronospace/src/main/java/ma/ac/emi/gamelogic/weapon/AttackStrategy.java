package ma.ac.emi.gamelogic.weapon;

import ma.ac.emi.camera.CameraShakeDefinition;

public abstract class AttackStrategy {
	protected CameraShakeDefinition cameraShakeDefinition;
	
	public AttackStrategy(CameraShakeDefinition cameraShakeDefinition) {
		this.cameraShakeDefinition = cameraShakeDefinition;
	}
    public abstract void execute(Weapon weapon, double step);
}