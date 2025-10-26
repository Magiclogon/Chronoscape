package ma.ac.emi.gamelogic.attack;

import ma.ac.emi.gamelogic.attack.type.ProjectileType;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.math.Vector2D;

public class ProjectileSingleHit extends Projectile{
	public ProjectileSingleHit(Vector2D pos, Vector2D dir, ProjectileType projectileType, Weapon weapon) {
		super(pos, dir, projectileType, weapon);
	}

	@Override
	public void applyEffect(LivingEntity entity) {
		entity.setHp(Math.max(0, entity.getHp() - this.getWeapon().getDamage()));
		System.out.println("Target hit, damage: " + this.getWeapon().getDamage() + ", remaining hp: " + entity.getHp());
		super.applyEffect(entity);
	}

	@Override
	public void onDesactivate() {
		// TODO Auto-generated method stub
		
	}
}
