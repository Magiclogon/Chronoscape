package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;
import java.util.Map;


public class LuckyPennyEffect implements PlayerEffect {

    private double luckPerWave      = 0.2;
    private double maxBonus         = 2.0;
    private double accumulatedBonus = 0;

    @Override
    public void configure(Map<String, Double> p) {
        luckPerWave = PlayerEffect.param(p, "luckPerWave", 0.2);
        maxBonus    = PlayerEffect.param(p, "maxBonus",    2.0);
    }

    @Override
    public void onRegister(Player player) {
        // Re-apply accumulated bonus — called again if inventory recalculates
        // and re-registers effects (e.g. after buying another item)
        if (accumulatedBonus > 0)
            player.setLuck(player.getLuck() + accumulatedBonus);
    }

    @Override
    public void onWaveEnd(Player player) {
        if (accumulatedBonus >= maxBonus) return;
        double gain = Math.min(luckPerWave, maxBonus - accumulatedBonus);
        accumulatedBonus += gain;
        player.setLuck(player.getLuck() + gain);
    }

    @Override
    public void onUnregister(Player player) {
        player.setLuck(player.getLuck() - accumulatedBonus);
        accumulatedBonus = 0;
    }
}