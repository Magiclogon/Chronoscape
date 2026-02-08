package ma.ac.emi.gamelogic.weapon.behavior;

import ma.ac.emi.gamelogic.weapon.Weapon;

public abstract class WeaponBehavior {
	protected double offset;
	
	public WeaponBehavior(double offset) {
		this.offset = offset;
	}
	
	public abstract void onInit(Weapon weapon);
	public abstract void onUpdate(Weapon weapon, double step);
	public abstract void onAttack(Weapon weapon, double step);
}
