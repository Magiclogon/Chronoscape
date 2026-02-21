package ma.ac.emi.gamelogic.attack.behavior;

import ma.ac.emi.gamelogic.attack.Projectile;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.math.Vector3D;
import ma.ac.emi.world.Obstacle;

public class DamageBehavior implements Behavior{
	private boolean destroyOnHit;
	
	public DamageBehavior(boolean destroyOnHit) {
		this.destroyOnHit = destroyOnHit;
	}
	@Override
	public void onUpdate(Projectile p, double step) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onHit(Projectile p, LivingEntity entity) {
		if(entity == null) return;
		WeaponItemDefinition definition = (WeaponItemDefinition) p.getWeapon().getWeaponItem().getItemDefinition();
		if(!entity.isInvincible()) {
			double damage = definition.getDamage();
			if (p.getWeapon().getBearer() instanceof Ennemy) {
				damage *= ((Ennemy) p.getWeapon().getBearer()).getDamage();
			}
			entity.setHp(Math.max(0, entity.getHp() - damage));
			System.out.println("Target hit, damage: " + damage + ", remaining hp: " + entity.getHp());

			double knockback = definition.getKnockbackForce();
			if(knockback != 0) {
				Vector3D kbDir = p.getVelocity().normalize();
				entity.applyKnockback(kbDir.mult(knockback));
			}

			entity.onHit();
		}
	}

	@Override
	public void onDesactivate(Projectile p) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onInit(Projectile p) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onHit(Projectile p, Obstacle obstacle) {
		// TODO Auto-generated method stub
		
	}

}
