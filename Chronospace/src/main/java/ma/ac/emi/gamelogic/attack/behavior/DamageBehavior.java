package ma.ac.emi.gamelogic.attack.behavior;

import ma.ac.emi.gamelogic.attack.Projectile;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;

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
		WeaponItemDefinition definition = (WeaponItemDefinition) p.getWeapon().getWeaponItem().getItemDefinition();
		if(!entity.isInvincible()) {
			entity.setHp(Math.max(0, entity.getHp() - definition.getDamage()));
			System.out.println("Target hit, damage: " + definition.getDamage() + ", remaining hp: " + entity.getHp());
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

}
