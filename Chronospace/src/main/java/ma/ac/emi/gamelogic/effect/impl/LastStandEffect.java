package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;
import java.util.Map;

/**
 * Last Stand — take less damage when below an HP threshold.
 * Params: hpThreshold (default 0.5), damageReduction (default 0.20)
 */
public class LastStandEffect implements PlayerEffect {

    private double hpThreshold     = 0.5;
    private double damageReduction = 0.20;

    @Override
    public void configure(Map<String, Double> p) {
        hpThreshold     = PlayerEffect.param(p, "hpThreshold",     0.5);
        damageReduction = PlayerEffect.param(p, "damageReduction", 0.20);
    }

    @Override
    public double onDamageTaken(Player player, double damage) {
        if (player.getHpMax() > 0 &&
                player.getHp() / player.getHpMax() < hpThreshold)
            return damage * (1.0 - damageReduction);
        return damage;
    }
}