package ma.ac.emi.gamelogic.weapon;

import ma.ac.emi.gamelogic.attack.ProjectileAOE;
import ma.ac.emi.gamelogic.attack.type.ProjectileAOEFactory;

public class MeleeAOEStrategy implements AttackStrategy {
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
            weapon.setTsla(0);
        }
    }
}