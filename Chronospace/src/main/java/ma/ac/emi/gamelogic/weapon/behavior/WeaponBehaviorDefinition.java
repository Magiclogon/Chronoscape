package ma.ac.emi.gamelogic.weapon.behavior;

public abstract class WeaponBehaviorDefinition {
	protected double offsetX, offsetY;
	public WeaponBehaviorDefinition(double offsetX, double offsetY) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	public abstract WeaponBehavior create();
}
