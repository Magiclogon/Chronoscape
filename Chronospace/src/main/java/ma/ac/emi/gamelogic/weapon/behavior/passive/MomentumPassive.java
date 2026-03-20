package ma.ac.emi.gamelogic.weapon.behavior.passive;

import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.weapon.Weapon;

/**
 * Momentum — damage increases with consecutive hits this wave.
 * Every N hits, gain +X% strength. Resets on wave start (switch-in resets too).
 *
 * Designed for: Sword, Fists, Lightsaber (melee that rewards staying in combat)
 *
 * Params:
 *   hitsPerStack    (default 5)    — hits needed per stack
 *   bonusPerStack   (default 0.08) — strength multiplier per stack
 *   maxStacks       (default 6)    — cap
 */
public class MomentumPassive extends WeaponPassive {

    private int    hitsPerStack  = 5;
    private double bonusPerStack = 0.08;
    private int    maxStacks     = 6;

    private int    hitCount      = 0;
    private int    currentStacks = 0;

    @Override
    public void configure(java.util.Map<String, Double> p) {
        super.configure(p);
        hitsPerStack  = paramInt("hitsPerStack",  5);
        bonusPerStack = param("bonusPerStack", 0.08);
        maxStacks     = paramInt("maxStacks",     6);
    }

    @Override
    public void onSwitchIn(Weapon weapon) {
        // Reset stacks on switch — momentum is lost when you put the weapon away
        Player player = player(weapon);
        if (player == null) return;
        removeStacks(player);
        hitCount = 0;
    }

    @Override
    public void onSwitchOut(Weapon weapon) {
        Player player = player(weapon);
        if (player != null) removeStacks(player);
        hitCount = 0;
    }

    @Override
    public void onAttack(Weapon weapon, double step) {
        Player player = player(weapon);
        if (player == null || currentStacks >= maxStacks) return;

        hitCount++;
        if (hitCount % hitsPerStack == 0) {
            player.setStrength(player.getStrength() * (1.0 + bonusPerStack));
            currentStacks++;
        }
    }

    private void removeStacks(Player player) {
        if (currentStacks > 0) {
            double totalMul = Math.pow(1.0 + bonusPerStack, currentStacks);
            player.setStrength(player.getStrength() / totalMul);
            currentStacks = 0;
        }
    }
}
