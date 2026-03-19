package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;

/**
 * Slippery When Wet — each successful dodge permanently stacks +0.5% dodge,
 * up to a max of +10% extra. Resets each wave to prevent runaway stacking.
 */
public class DodgeStackEffect implements PlayerEffect {

    private static final double DODGE_PER_STACK = 0.005;
    private static final double MAX_STACKED     = 0.10;

    private double stacked = 0;

    @Override
    public void onDodge(Player player) {
        if (stacked >= MAX_STACKED) return;
        double gain = Math.min(DODGE_PER_STACK, MAX_STACKED - stacked);
        stacked += gain;
        player.setDodge(player.getDodge() + gain);
    }

    @Override
    public void onWaveStart(Player player) {
        player.setDodge(Math.max(0, player.getDodge() - stacked));
        stacked = 0;
    }

    @Override
    public void onUnregister(Player player) {
        player.setDodge(Math.max(0, player.getDodge() - stacked));
        stacked = 0;
    }
}
