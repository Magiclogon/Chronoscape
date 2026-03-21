package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.player.Player;
import java.util.Map;

/**
 * Crimson Belt — lifesteal scales with missing HP.
 * At full HP: minRate lifesteal. Near death: maxRate lifesteal.
 * Params: minRate (default 0.05), maxRate (default 0.25)
 */
public class CrimsonBeltEffect implements PlayerEffect {

    private double minRate = 0.05;
    private double maxRate = 0.25;

    @Override
    public void configure(Map<String, Double> p) {
        minRate = PlayerEffect.param(p, "minRate", 0.05);
        maxRate = PlayerEffect.param(p, "maxRate", 0.25);
    }

    @Override
    public void onDamageDealt(Player player, LivingEntity target, double damageDealt) {
        if (player.getHpMax() <= 0) return;
        double missingFraction = 1.0 - (player.getHp() / player.getHpMax());
        double rate = minRate + missingFraction * (maxRate - minRate);
        player.heal(damageDealt * rate);
    }
}