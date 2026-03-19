package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;

/**
 * Battle Hardened — gain +5 armor permanently for every wave completed,
 * up to a maximum of +50.
 */
public class BattleHardenedEffect implements PlayerEffect {

    private static final double ARMOR_PER_WAVE = 5.0;
    private static final double MAX_BONUS       = 50.0;

    private double accumulatedArmor = 0;

    @Override
    public void onWaveEnd(Player player) {
        if (accumulatedArmor >= MAX_BONUS) return;
        double gain = Math.min(ARMOR_PER_WAVE, MAX_BONUS - accumulatedArmor);
        accumulatedArmor += gain;
        player.setDefense(player.getDefense() + gain);
    }

    @Override
    public void onUnregister(Player player) {
        player.setDefense(player.getDefense() - accumulatedArmor);
        accumulatedArmor = 0;
    }
}
