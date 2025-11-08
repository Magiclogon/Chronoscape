package ma.ac.emi.gamelogic.attack;

import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.attack.type.AOEFactory;
import ma.ac.emi.gamelogic.attack.type.ProjectileAOEDefinition;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.math.Vector3D;

public class ProjectileAOE extends Projectile{

	public ProjectileAOE(Vector3D pos, Vector3D dir, ProjectileAOEDefinition projectileType, Weapon weapon) {
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
		AOE aoe = AOEFactory.getInstance().createAOE(
				((ProjectileAOEDefinition)getProjectileType()).getAoeId(), 
				pos, 
				getWeapon());
		weapon.getAttackObjectManager().addObject(aoe);
	}

	@Override
	public void onDesactivate() {
		super.onDesactivate();
		spawnAoe();
	}
	

}
