package ma.ac.emi.gamelogic.attack;

import ma.ac.emi.gamelogic.attack.type.ProjectileType;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.math.Vector2D;

public class ProjectileAOE extends Projectile{

	public ProjectileAOE(Vector2D pos, Vector2D dir, ProjectileType projectileType, Weapon weapon) {
		super(pos, dir, projectileType, weapon);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void update(double step) {
		super.update(step);
	}
	
	@Override
	public void applyEffect(LivingEntity entity) {
		super.applyEffect(entity);
	}
	
	public void spawnAoe() {
		Weapon weapon = getWeapon();
		AOE aoe = new AOE(getPos(), weapon.getAoeType(), weapon);
		weapon.getAttackObjectManager().addObject(aoe);
	}

	@Override
	public void onDesactivate() {
		spawnAoe();
	}
	

}
