package ma.ac.emi.gamelogic.weapon;

import ma.ac.emi.gamelogic.attack.ProjectileSingleHit;

public class MeleeSingleHitStrategy implements AttackStrategy {
    @Override
    public void execute(Weapon weapon) {
        if (weapon.getTsla() >= 1/weapon.getAttackSpeed()) {
            ProjectileSingleHit projectile = new ProjectileSingleHit(
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