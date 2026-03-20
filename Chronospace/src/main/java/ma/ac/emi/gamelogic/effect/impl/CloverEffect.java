package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;
import java.util.Map;

/** Params: healPerDodge (default 5.0) */
public class CloverEffect implements PlayerEffect {
    private double healPerDodge = 5.0;

    @Override public void configure(Map<String, Double> p) {
        healPerDodge = PlayerEffect.param(p, "healPerDodge", 5.0);
    }

    @Override public void onDodge(Player player) {
        player.setHp(Math.min(player.getHp() + healPerDodge, player.getHpMax()));
    }
}