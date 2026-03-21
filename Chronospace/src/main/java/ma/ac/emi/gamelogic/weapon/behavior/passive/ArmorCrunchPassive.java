package ma.ac.emi.gamelogic.weapon.behavior.passive;

import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.gamelogic.weapon.Weapon;

public class ArmorCrunchPassive extends WeaponPassive {

    private double damagePerArmor = 0.05;
    private double lastFlat       = 0;
    private boolean active        = false;

    @Override
    public void configure(java.util.Map<String, Double> p) {
        super.configure(p);
        damagePerArmor = param("damagePerArmor", 0.05);
    }

    @Override
    public void onSwitchIn(Weapon weapon)  { active = true; lastFlat = 0; }

    @Override
    public void onSwitchOut(Weapon weapon) {
        if (lastFlat != 0) {
            WeaponItemDefinition def = (WeaponItemDefinition) weapon.getWeaponItem().getItemDefinition();
            def.setDamage(def.getDamage() - lastFlat);
        }
        lastFlat = 0;
        active   = false;
    }

    @Override
    public void onWaveEnd(Weapon weapon) {
        if (lastFlat != 0) {
            WeaponItemDefinition def = (WeaponItemDefinition) weapon.getWeaponItem().getItemDefinition();
            def.setDamage(def.getDamage() - lastFlat);
        }
        lastFlat = 0;
        active   = false;
    }
    public void onUpdate(Weapon weapon, double step) {
        if (!active) return;
        Player player = player(weapon);
        if (player == null) return;

        double targetFlat = player.getDefense() * damagePerArmor;
        if (Math.abs(targetFlat - lastFlat) < 0.001) return;

        WeaponItemDefinition def = (WeaponItemDefinition) weapon.getWeaponItem().getItemDefinition();
        def.setDamage(def.getDamage() - lastFlat + targetFlat);
        lastFlat = targetFlat;
    }
    
    @Override
    public String describe() {
        double dpa = param("damagePerArmor", 0.05);
        return String.format("+%.2f damage per armor point", dpa);
    }
}