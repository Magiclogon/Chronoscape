package ma.ac.emi.gamelogic.weapon.behavior.passive;

import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.weapon.Weapon;

/**
 * Armor Crunch — while equipped, each point of armor above base
 * converts into a small flat damage bonus.
 * Designed for: Hammer, Shotgun (tanks that want to deal too)
 *
 * Params:
 *   damagePerArmor (default 0.05) — strength added per armor point
 *                                   e.g. 60 armor → +3 strength
 */
public class ArmorCrunchPassive extends WeaponPassive {

    private double damagePerArmor = 0.05;
    private double lastBonus      = 0;
    private boolean active        = false;

    @Override
    public void configure(java.util.Map<String, Double> p) {
        super.configure(p);
        damagePerArmor = param("damagePerArmor", 0.05);
    }

    @Override
    public void onSwitchIn(Weapon weapon)  { active = true; lastBonus = 0; }

    @Override
    public void onSwitchOut(Weapon weapon) {
        Player player = player(weapon);
        if (player != null)
            player.setStrength(player.getStrength() - lastBonus);
        lastBonus = 0;
        active = false;
    }

    @Override
    public void onUpdate(Weapon weapon, double step) {
        if (!active) return;
        Player player = player(weapon);
        if (player == null) return;

        double bonus = player.getDefense() * damagePerArmor;
        double delta = bonus - lastBonus;
        if (Math.abs(delta) > 0.001) {
            player.setStrength(player.getStrength() + delta);
            lastBonus = bonus;
        }
    }
}
