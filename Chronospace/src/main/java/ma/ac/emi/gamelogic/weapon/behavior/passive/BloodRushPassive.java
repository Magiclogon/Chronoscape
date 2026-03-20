package ma.ac.emi.gamelogic.weapon.behavior.passive;

import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.weapon.Weapon;

/**
 * Blood Rush — while equipped, attack speed scales with missing HP.
 * At full HP: no bonus. At 25% HP: max bonus.
 * Designed for: Fists, Lightsaber (high-risk melee fantasy)
 *
 * Params:
 *   maxBonus (default 0.60) — max attack speed multiplier bonus at 0 HP
 *
 * Since attack speed lives on weapons, we apply it as strength (damage output)
 * scaling — the player fights harder when desperate.
 */
public class BloodRushPassive extends WeaponPassive {

    private double maxBonus   = 0.60;
    private double lastFactor = 1.0;
    private boolean active    = false;

    @Override
    public void configure(java.util.Map<String, Double> p) {
        super.configure(p);
        maxBonus = param("maxBonus", 0.60);
    }

    @Override
    public void onSwitchIn(Weapon weapon)  { active = true; lastFactor = 1.0; }

    @Override
    public void onSwitchOut(Weapon weapon) {
        Player player = player(weapon);
        if (player != null && lastFactor != 1.0)
            player.setStrength(player.getStrength() / lastFactor);
        lastFactor = 1.0;
        active = false;
    }

    @Override
    public void onUpdate(Weapon weapon, double step) {
        if (!active) return;
        Player player = player(weapon);
        if (player == null || player.getHpMax() <= 0) return;

        double missingFraction = 1.0 - (player.getHp() / player.getHpMax());
        double factor = 1.0 + maxBonus * missingFraction;

        if (Math.abs(factor - lastFactor) > 0.001) {
            player.setStrength(player.getStrength() / lastFactor * factor);
            lastFactor = factor;
        }
    }
}
