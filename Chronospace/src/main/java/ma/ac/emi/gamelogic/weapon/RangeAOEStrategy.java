package ma.ac.emi.gamelogic.weapon;

import ma.ac.emi.gamelogic.attack.ProjectileAOE;

public class RangeAOEStrategy implements AttackStrategy {
    @Override
    public void execute(Weapon weapon) {
        if (weapon.getTsla() >= 1/weapon.getAttackSpeed() && weapon.getAmmo() > 0) {
            ProjectileAOE projectile = new ProjectileAOE(
                weapon.getPos(), 
                weapon.getDir(),
                weapon.getProjectileType(),
                weapon
            );
            weapon.getAttackObjectManager().addObject(projectile);
            weapon.setAmmo(weapon.getAmmo() - 1);
            weapon.setTsla(0);
        }
    }
}