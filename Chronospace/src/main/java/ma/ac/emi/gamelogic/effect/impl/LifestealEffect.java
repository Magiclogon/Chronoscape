package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.player.Player;
import java.util.Map;

/** Params: rate (default 0.06) */
public class LifestealEffect implements PlayerEffect {
    protected double rate = 0.06;

    @Override public void configure(Map<String, Double> p) {
        rate = PlayerEffect.param(p, "rate", 0.06);
    }

    @Override public void onDamageDealt(Player player, LivingEntity target, double damageDealt) {
        double heal = damageDealt * rate;
        if (heal > 0) player.setHp(Math.min(player.getHp() + heal, player.getHpMax()));
    }
}