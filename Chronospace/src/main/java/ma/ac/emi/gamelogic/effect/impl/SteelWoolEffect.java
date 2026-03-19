package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;

/**
 * Steel Wool — for every 20 armor (defense) the player has,
 * grant +5% attack speed. Recalculates every tick.
 *
 * Example: 60 armor → 3 steps → +15% attack speed bonus on strength.
 * Since attack speed lives on weapons rather than player stats, we apply
 * the bonus as a strength multiplier that scales weapon effectiveness.
 *
 * Note: if you later expose a player-level attackSpeedMultiplier field,
 * replace strength scaling with that.
 */
public class SteelWoolEffect implements PlayerEffect {

    private static final double ARMOR_PER_STEP        = 20.0;
    private static final double ATTACK_SPEED_PER_STEP = 0.05; // 5%

    private double lastAppliedMultiplier = 1.0;

    @Override
    public void onTick(Player player, double step) {
        double steps      = Math.floor(player.getDefense() / ARMOR_PER_STEP);
        double multiplier = 1.0 + (steps * ATTACK_SPEED_PER_STEP);

        if (Math.abs(multiplier - lastAppliedMultiplier) > 0.001) {
            // Undo previous multiplier, apply new one
            player.setStrength(player.getStrength() / lastAppliedMultiplier * multiplier);
            lastAppliedMultiplier = multiplier;
        }
    }

    @Override
    public void onUnregister(Player player) {
        player.setStrength(player.getStrength() / lastAppliedMultiplier);
        lastAppliedMultiplier = 1.0;
    }
}
