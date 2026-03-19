package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.player.Player;

/**
 * Killing Spree — each kill grants +2 flat weapon damage for the rest of the wave.
 * Bonus resets on wave start (effect is unregistered and re-registered each round).
 */
public class KillingSpreeEffect implements PlayerEffect {

    private static final double DAMAGE_PER_KILL = 2.0;
    private double accumulatedBonus = 0;

    @Override
    public void onKill(Player player, LivingEntity killed) {
        accumulatedBonus += DAMAGE_PER_KILL;
        // Apply directly to strength so it feeds into weapon damage calculations
        player.setStrength(player.getStrength() + DAMAGE_PER_KILL);
    }

    @Override
    public void onUnregister(Player player) {
        // Remove the accumulated bonus when item is sold
        player.setStrength(player.getStrength() - accumulatedBonus);
        accumulatedBonus = 0;
    }

    @Override
    public void onWaveStart(Player player) {
        // Reset the wave bonus at the start of each new wave
        player.setStrength(player.getStrength() - accumulatedBonus);
        accumulatedBonus = 0;
    }
}
