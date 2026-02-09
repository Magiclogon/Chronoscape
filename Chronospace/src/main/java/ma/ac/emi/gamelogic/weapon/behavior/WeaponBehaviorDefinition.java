package ma.ac.emi.gamelogic.weapon.behavior;

public abstract class WeaponBehaviorDefinition {
	protected double offset;
	public WeaponBehaviorDefinition(double offset) {
		this.offset = offset;
	}

	public abstract WeaponBehavior create();
}
