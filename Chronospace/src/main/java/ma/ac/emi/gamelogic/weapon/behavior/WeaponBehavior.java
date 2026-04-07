package ma.ac.emi.gamelogic.weapon.behavior;

import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.weapon.Weapon;

public abstract class WeaponBehavior {
	protected double offsetX;
	protected double offsetY;
	
	public WeaponBehavior(double offsetX, double offsetY) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}
	
	public void onInit(Weapon weapon) {}
	public void onUpdate(Weapon weapon, double step) {}
	public void onAttack(Weapon weapon, double step) {}
	public void onKill(Weapon weapon, LivingEntity killed) {}
	public void onSwitchIn(Weapon weapon) {}
	public void onSwitchOut(Weapon weapon) {}
	public void onWaveStart(Weapon weapon) {}
	public void onWaveEnd(Weapon weapon)   {}
}