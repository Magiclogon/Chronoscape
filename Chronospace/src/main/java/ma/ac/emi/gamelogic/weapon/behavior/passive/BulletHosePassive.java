package ma.ac.emi.gamelogic.weapon.behavior.passive;

import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.ItemLoader;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.gamelogic.weapon.Weapon;

/**
 * Bullet Hose — magazine size scales with the player's current attack speed bonus.
 * The faster you shoot, the more bullets you carry per magazine.
 *
 * Formula: effectiveMag = baseMag * (1 + attackSpeedBonus * scaleFactor)
 * where attackSpeedBonus = (currentAttackSpeed / baseAttackSpeed) - 1
 *
 * Example: base mag 30, base atk speed 10, current atk speed 14 (+40% bonus)
 *   with scaleFactor 1.0 → effectiveMag = 30 * (1 + 0.4) = 42
 *
 * Designed for: AK47 (high fire rate weapon that benefits from attack speed items)
 *
 * Params:
 *   scaleFactor (default 1.0) — how much each 100% attack speed bonus adds to mag
 */
public class BulletHosePassive extends WeaponPassive {

    private double scaleFactor   = 1.0;
    private int    lastMagBonus  = 0;
    private boolean active       = false;

    @Override
    public void configure(java.util.Map<String, Double> p) {
        super.configure(p);
        scaleFactor = param("scaleFactor", 1.0);
    }

    @Override
    public void onSwitchIn(Weapon weapon)  { active = true; lastMagBonus = 0; }

    @Override
    public void onSwitchOut(Weapon weapon) {
        if (lastMagBonus != 0) {
            WeaponItemDefinition def = (WeaponItemDefinition) weapon.getWeaponItem().getItemDefinition();
            def.setMagazineSize(def.getMagazineSize() - lastMagBonus);
        }
        lastMagBonus = 0;
        active = false;
    }

    @Override
    public void onWaveEnd(Weapon weapon) {
        if (lastMagBonus != 0) {
            WeaponItemDefinition def = (WeaponItemDefinition) weapon.getWeaponItem().getItemDefinition();
            def.setMagazineSize(def.getMagazineSize() - lastMagBonus);
        }
        lastMagBonus = 0;
        active = false;
    }

    @Override
    public void onUpdate(Weapon weapon, double step) {
        if (!active) return;
        Player player = player(weapon);
        if (player == null) return;

        WeaponItemDefinition def  = (WeaponItemDefinition) weapon.getWeaponItem().getItemDefinition();
        WeaponItemDefinition base = (WeaponItemDefinition)
                ItemLoader.getInstance().getBaseItemDefinition(def.getId());

        double baseAtkSpeed    = base.getAttackSpeed();
        double currentAtkSpeed = def.getAttackSpeed();
        if (baseAtkSpeed <= 0) return;

        double atkSpeedBonus = (currentAtkSpeed / baseAtkSpeed) - 1.0;
        if (atkSpeedBonus <= 0) {
            // No bonus — undo any previous mag increase
            if (lastMagBonus != 0) {
                def.setMagazineSize(def.getMagazineSize() - lastMagBonus);
                lastMagBonus = 0;
            }
            return;
        }

        int targetMagBonus = (int)(base.getMagazineSize() * atkSpeedBonus * scaleFactor);
        int delta = targetMagBonus - lastMagBonus;
        if (delta == 0) return;

        def.setMagazineSize(def.getMagazineSize() + delta);
        lastMagBonus = targetMagBonus;
    }
    
    @Override
    public String describe() {
        return String.format("Magazine size scales with attack speed (x%.1f)", scaleFactor);
    }

}