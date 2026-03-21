package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.player.Player;
import java.util.Map;

/** Params: healAmount (default 2.0) */
public class VampirismEffect implements PlayerEffect {
    private double healAmount = 2.0;

    @Override public void configure(Map<String, Double> p) {
        healAmount = PlayerEffect.param(p, "healAmount", 2.0);
    }

    @Override public void onKill(Player player, LivingEntity killed) {
        player.heal(healAmount);
    }
}