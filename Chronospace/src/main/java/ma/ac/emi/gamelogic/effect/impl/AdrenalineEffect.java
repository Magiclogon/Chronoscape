package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.player.Player;
import java.util.Map;

/** Params: killsRequired (default 5), boostFactor (default 1.4), boostDuration (default 2.0) */
public class AdrenalineEffect implements PlayerEffect {
    private int    killsRequired = 5;
    private double boostFactor   = 1.4;
    private double boostDuration = 2.0;

    private int    killCount      = 0;
    private double boostRemaining = 0;
    private boolean boosted       = false;

    @Override public void configure(Map<String, Double> p) {
        killsRequired = PlayerEffect.paramInt(p, "killsRequired", 5);
        boostFactor   = PlayerEffect.param(p,    "boostFactor",   1.4);
        boostDuration = PlayerEffect.param(p,    "boostDuration", 2.0);
    }

    @Override public void onKill(Player player, LivingEntity killed) {
        if (++killCount % killsRequired == 0) {
            if (!boosted) { player.setSpeed(player.getSpeed() * boostFactor); boosted = true; }
            boostRemaining = boostDuration;
        }
    }

    @Override public void onTick(Player player, double step) {
        if (!boosted) return;
        boostRemaining -= step;
        if (boostRemaining <= 0) {
            player.setSpeed(player.getSpeed() / boostFactor);
            boosted = false; boostRemaining = 0;
        }
    }

    @Override public void onUnregister(Player player) {
        if (boosted) { player.setSpeed(player.getSpeed() / boostFactor); boosted = false; }
    }
}