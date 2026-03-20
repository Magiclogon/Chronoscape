package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;
import java.util.Map;

/** Params: boostFactor (default 1.4), boostDuration (default 1.5) */
public class PhantomStepEffect implements PlayerEffect {
    private double boostFactor   = 1.4;
    private double boostDuration = 1.5;
    private double boostRemaining = 0;
    private boolean boosted = false;

    @Override public void configure(Map<String, Double> p) {
        boostFactor   = PlayerEffect.param(p, "boostFactor",   1.4);
        boostDuration = PlayerEffect.param(p, "boostDuration", 1.5);
    }

    @Override public void onDodge(Player player) {
        if (!boosted) { player.setSpeed(player.getSpeed() * boostFactor); boosted = true; }
        boostRemaining = boostDuration;
    }

    @Override public void onTick(Player player, double step) {
        if (!boosted) return;
        boostRemaining -= step;
        if (boostRemaining <= 0) { player.setSpeed(player.getSpeed() / boostFactor); boosted = false; boostRemaining = 0; }
    }

    @Override public void onUnregister(Player player) {
        if (boosted) { player.setSpeed(player.getSpeed() / boostFactor); boosted = false; }
    }
}