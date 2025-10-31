package ma.ac.emi.gamelogic.weapon;

import ma.ac.emi.gamelogic.attack.ProjectileAOE;

public class MeleeAOEStrategy implements AttackStrategy {
    @Override
    public void execute(Weapon weapon) {
        if (weapon.getTsla() >= 1/weapon.getAttackSpeed()) {
        	ProjectileAOE projectile = new ProjectileAOE(
                    weapon.getPos(), 
                    weapon.getDir(), 
                    weapon.getProjectileType(), 
                    weapon
                );
            weapon.getAttackObjectManager().addObject(projectile);
            weapon.setTsla(0);
        }
    }
}