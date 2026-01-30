package ma.ac.emi.gamelogic.weapon;

import ma.ac.emi.gamelogic.attack.Projectile;
import ma.ac.emi.gamelogic.attack.type.ProjectileFactory;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.math.Vector3D;

public class RangeStrategy implements AttackStrategy {
	private int projectileCount;
	private double spread;
	
	public RangeStrategy(int projectileCount, double spread) {
		this.projectileCount = projectileCount;
		this.spread = spread;
	}
	
    @Override
    public void execute(Weapon weapon) {
    	WeaponItemDefinition definition = ((WeaponItemDefinition) weapon.getWeaponItem().getItemDefinition());
        if (weapon.getTsla() >= 1/definition.getAttackSpeed() && weapon.getAmmo() > 0) {
        	for(int i = 0; i < projectileCount; i++) {
        		double angle = weapon.getDir().getAngle() + Math.random()*spread - spread/2;
        		Vector3D dir = new Vector3D(Math.cos(angle), Math.sin(angle));
        		
        		Projectile projectile = ProjectileFactory.createProjectile(
            			definition.getProjectileId(),
            			weapon.getPos().add(weapon.getRelativeProjectilePos()),
            			dir,
            			weapon
            		);
                weapon.getAttackObjectManager().addObject(projectile);
                weapon.consumeAmmo();
        	}
        	
            weapon.setTsla(0);
            weapon.getStateMachine().getCurrentAnimationState().reset();
        }
    }
}