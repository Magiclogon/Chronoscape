package ma.ac.emi.gamelogic.weapon.behavior.passive;

import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.gamelogic.weapon.Weapon;

public class BloodRushPassive extends WeaponPassive {

    private double maxBonus        = 0.60;
    private double lastMultiplier  = 1.0;
    private boolean active         = false;

    @Override
    public void configure(java.util.Map<String, Double> p) {
        super.configure(p);
        maxBonus = param("maxBonus", 0.60);
    }

    @Override
    public void onSwitchIn(Weapon weapon)  { active = true; lastMultiplier = 1.0; }

    @Override
    public void onSwitchOut(Weapon weapon) {
        if (lastMultiplier != 1.0) {
            WeaponItemDefinition def = (WeaponItemDefinition) weapon.getWeaponItem().getItemDefinition();
            def.setDamage(def.getDamage() / lastMultiplier);
        }
        lastMultiplier = 1.0;
        active = false;
    }

    @Override
    public void onWaveEnd(Weapon weapon) {
        if (lastMultiplier != 1.0) {
            WeaponItemDefinition def = (WeaponItemDefinition) weapon.getWeaponItem().getItemDefinition();
            def.setDamage(def.getDamage() / lastMultiplier);
        }
        lastMultiplier = 1.0;
        active = false;
    }
    public void onUpdate(Weapon weapon, double step) {
        if (!active) return;
        Player player = player(weapon);
        if (player == null || player.getHpMax() <= 0) return;

        double missingFraction  = 1.0 - (player.getHp() / player.getHpMax());
        double targetMultiplier = 1.0 + maxBonus * missingFraction;
        if (Math.abs(targetMultiplier - lastMultiplier) < 0.0001) return;

        WeaponItemDefinition def = (WeaponItemDefinition) weapon.getWeaponItem().getItemDefinition();
        def.setDamage(def.getDamage() / lastMultiplier * targetMultiplier);
        lastMultiplier = targetMultiplier;
    }
    
    @Override
    public String describe() {
        int maxPct = (int) Math.round(param("maxBonus", 0.60) * 100);
        return String.format("Up to +%d%% damage as HP drops", maxPct);
    }
}