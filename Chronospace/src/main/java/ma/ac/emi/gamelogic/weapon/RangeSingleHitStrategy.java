package ma.ac.emi.gamelogic.weapon;

import ma.ac.emi.gamelogic.attack.ProjectileSingleHit;

public class RangeSingleHitStrategy implements AttackStrategy {
    @Override
    public void execute(Weapon weapon) {
        if (weapon.getTsla() >= 1/weapon.getAttackSpeed() && weapon.getAmmo() > 0) {
            ProjectileSingleHit projectile = new ProjectileSingleHit(
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