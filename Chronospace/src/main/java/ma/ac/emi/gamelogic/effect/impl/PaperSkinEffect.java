package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;
import java.util.Map;

/**
 * Paper Skin — for every 10% speed above base, take more damage.
 * Params: damageIncreasePerTenPct (default 0.03)
 */
public class PaperSkinEffect implements PlayerEffect {

    private double damageIncreasePerTenPct = 0.03;

    @Override
    public void configure(Map<String, Double> p) {
        damageIncreasePerTenPct = PlayerEffect.param(p, "damageIncreasePerTenPct", 0.03);
    }

    @Override
    public double onDamageTaken(Player player, double damage) {
        double speedRatio = player.getSpeed() / player.getBaseSpeed();
        if (speedRatio <= 1.0) return damage;
        double tenths    = Math.floor((speedRatio - 1.0) / 0.10);
        return damage * (1.0 + tenths * damageIncreasePerTenPct);
    }
}