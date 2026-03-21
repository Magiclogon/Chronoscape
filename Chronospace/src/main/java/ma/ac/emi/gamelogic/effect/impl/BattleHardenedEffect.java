package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;
import java.util.Map;

/**
 * Battle Hardened — gain flat armor permanently for every wave completed, up to a cap.
 * Params: armorPerWave (default 5.0), maxBonus (default 50.0)
 */
public class BattleHardenedEffect implements PlayerEffect {

    private double armorPerWave     = 5.0;
    private double maxBonus         = 50.0;
    private double accumulatedArmor = 0;

    @Override
    public void configure(Map<String, Double> p) {
        armorPerWave = PlayerEffect.param(p, "armorPerWave", 5.0);
        maxBonus     = PlayerEffect.param(p, "maxBonus",     50.0);
    }

    @Override
    public void onWaveEnd(Player player) {
        if (accumulatedArmor >= maxBonus) return;
        double gain = Math.min(armorPerWave, maxBonus - accumulatedArmor);
        accumulatedArmor += gain;
        player.setDefense(player.getDefense() + gain);
    }

    @Override
    public void onUnregister(Player player) {
        player.setDefense(player.getDefense() - accumulatedArmor);
        accumulatedArmor = 0;
    }
}