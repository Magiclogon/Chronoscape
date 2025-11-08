package ma.ac.emi.gamelogic.attack;

import ma.ac.emi.gamelogic.attack.type.ProjectileSingleHitDefinition;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.math.Vector3D;

public class ProjectileSingleHit extends Projectile{
	public ProjectileSingleHit(Vector3D pos, Vector3D dir, ProjectileSingleHitDefinition projectileType, Weapon weapon) {
		super(pos, dir, projectileType, weapon);
	}

	@Override
	public void applyEffect(LivingEntity entity) {
    	WeaponItemDefinition definition = (WeaponItemDefinition) getWeapon().getWeaponItem().getItemDefinition();
		entity.setHp(Math.max(0, entity.getHp() - definition.getDamage()));
		System.out.println("Target hit, damage: " + definition.getDamage() + ", remaining hp: " + entity.getHp());
		super.applyEffect(entity);
	}

}
