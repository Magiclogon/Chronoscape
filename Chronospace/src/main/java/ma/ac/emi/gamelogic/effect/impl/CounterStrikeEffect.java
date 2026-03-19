package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;

/**
 * Counter Strike — after dodging, next attack deals +100% bonus damage for 1.5s.
 * Rewards dodging right before attacking.
 */
public class CounterStrikeEffect implements PlayerEffect {

    private static final double DAMAGE_MULTIPLIER = 2.0;
    private static final double WINDOW_DURATION   = 1.5;

    private double windowRemaining = 0;
    private boolean active = false;

    @Override
    public void onDodge(Player player) {
        if (!active) {
            player.setStrength(player.getStrength() * DAMAGE_MULTIPLIER);
            active = true;
        }
        windowRemaining = WINDOW_DURATION; // refresh
    }

    @Override
    public void onTick(Player player, double step) {
        if (!active) return;
        windowRemaining -= step;
        if (windowRemaining <= 0) {
            player.setStrength(player.getStrength() / DAMAGE_MULTIPLIER);
            active = false;
            windowRemaining = 0;
        }
    }

    @Override
    public void onUnregister(Player player) {
        if (active) {
            player.setStrength(player.getStrength() / DAMAGE_MULTIPLIER);
            active = false;
        }
    }
}
