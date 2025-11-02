package ma.ac.emi.gamelogic.attack;

import ma.ac.emi.gamelogic.attack.type.ProjectileSingleHitDefinition;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.math.Vector2D;

public class ProjectileSingleHit extends Projectile{
	public ProjectileSingleHit(Vector2D pos, Vector2D dir, ProjectileSingleHitDefinition projectileType, Weapon weapon) {
		super(pos, dir, projectileType, weapon);
	}

	@Override
	public void applyEffect(LivingEntity entity) {
		entity.setHp(Math.max(0, entity.getHp() - this.getWeapon().getDefinition().getDamage()));
		System.out.println("Target hit, damage: " + this.getWeapon().getDefinition().getDamage() + ", remaining hp: " + entity.getHp());
		super.applyEffect(entity);
	}

	@Override
	public void onDesactivate() {
		// TODO Auto-generated method stub
		
	}
}
