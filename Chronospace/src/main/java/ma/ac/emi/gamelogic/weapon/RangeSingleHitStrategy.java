package ma.ac.emi.gamelogic.weapon;

import ma.ac.emi.gamelogic.attack.ProjectileSingleHit;
import ma.ac.emi.gamelogic.attack.type.ProjectileSingleHitFactory;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;

public class RangeSingleHitStrategy implements AttackStrategy {
    @Override
    public void execute(Weapon weapon) {
    	WeaponItemDefinition definition = ((WeaponItemDefinition) weapon.getWeaponItem().getItemDefinition());
        if (weapon.getTsla() >= 1/definition.getAttackSpeed() && !weapon.isInState("Reload")) {
        	ProjectileSingleHit projectile = (ProjectileSingleHit) ProjectileSingleHitFactory.getInstance().createProjectile(
        			definition.getProjectileId(),
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