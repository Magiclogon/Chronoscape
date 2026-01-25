package ma.ac.emi.gamelogic.weapon;

import ma.ac.emi.gamelogic.attack.Projectile;
import ma.ac.emi.gamelogic.attack.ProjectileAOE;
import ma.ac.emi.gamelogic.attack.type.ProjectileAOEFactory;
import ma.ac.emi.gamelogic.attack.type.ProjectileFactory;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;

public class MeleeAOEStrategy implements AttackStrategy {
    @Override
    public void execute(Weapon weapon) {
    	WeaponItemDefinition definition = ((WeaponItemDefinition) weapon.getWeaponItem().getItemDefinition());
        if (weapon.getTsla() >= 1/definition.getAttackSpeed()) {
        	Projectile projectile = ProjectileFactory.createProjectile(
        			definition.getProjectileId(),
        			weapon.getPos(),
        			weapon.getDir(),
        			weapon
        		);
            weapon.getAttackObjectManager().addObject(projectile);
            weapon.setTsla(0);
            weapon.getStateMachine().getCurrentAnimationState().reset();
        }
    }
}