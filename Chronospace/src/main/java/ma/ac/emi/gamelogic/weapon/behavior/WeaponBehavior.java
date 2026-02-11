package ma.ac.emi.gamelogic.weapon.behavior;

import ma.ac.emi.gamelogic.weapon.Weapon;

public abstract class WeaponBehavior {
	protected double offsetX;
	protected double offsetY;
	
	public WeaponBehavior(double offsetX, double offsetY) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}
	
	public abstract void onInit(Weapon weapon);
	public abstract void onUpdate(Weapon weapon, double step);
	public abstract void onAttack(Weapon weapon, double step);
	public abstract void onSwitchIn(Weapon weapon);
	public abstract void onSwitchOut(Weapon weapon);
}
