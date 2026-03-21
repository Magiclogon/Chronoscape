package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;
import java.util.Map;

/**
 * Life Insurance — heal a percentage of max HP at the end of each wave.
 * Rewards building a large HP pool since the heal scales with it.
 *
 * Params: healPercent (default 0.08 = 8% of max HP)
 */
public class LifeInsuranceEffect implements PlayerEffect {

    private double healPercent = 0.08;

    @Override
    public void configure(Map<String, Double> p) {
        healPercent = PlayerEffect.param(p, "healPercent", 0.08);
    }

    @Override
    public void onWaveEnd(Player player) {
        double heal = player.getHpMax() * healPercent;
        player.heal(heal);
    }
}
