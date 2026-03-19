package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;
import java.util.Map;

/** Params: reduction (default 0.08 = 8% less damage taken) */
public class ThornsEffect implements PlayerEffect {
    private double reduction = 0.08;

    @Override public void configure(Map<String, Double> p) {
        reduction = PlayerEffect.param(p, "reduction", 0.08);
    }

    @Override public double onDamageTaken(Player player, double damage) {
        return damage * (1.0 - reduction);
    }
}