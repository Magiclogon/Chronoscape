package ma.ac.emi.gamelogic.weapon;

import ma.ac.emi.gamelogic.attack.ProjectileAOE;
import ma.ac.emi.gamelogic.attack.type.ProjectileAOEFactory;

public class RangeAOEStrategy implements AttackStrategy {
    @Override
    public void execute(Weapon weapon) {
    	if (weapon.getTsla() >= 1/weapon.getDefinition().getAttackSpeed()) {
        	ProjectileAOE projectile = (ProjectileAOE) ProjectileAOEFactory.getInstance().createProjectile(
        			weapon.getDefinition().getProjectileId(),
        			weapon.getPos(),
        			weapon.getDir(),
        			weapon
        		);
            weapon.getAttackObjectManager().addObject(projectile);
            weapon.setAmmo(weapon.getAmmo() - 1);
            weapon.setTsla(0);
        }
    }
}