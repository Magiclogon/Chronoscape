package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;
import java.util.Map;

/** Params: damageMultiplier (default 1.8), windowDuration (default 1.0) */
public class CounterStrikeEffect implements PlayerEffect {
    private double damageMultiplier = 1.8;
    private double windowDuration   = 1.0;
    private double windowRemaining  = 0;
    private boolean active = false;

    @Override public void configure(Map<String, Double> p) {
        damageMultiplier = PlayerEffect.param(p, "damageMultiplier", 1.8);
        windowDuration   = PlayerEffect.param(p, "windowDuration",   1.0);
    }

    @Override public void onDodge(Player player) {
        if (!active) { player.setStrength(player.getStrength() * damageMultiplier); active = true; }
        windowRemaining = windowDuration;
    }

    @Override public void onTick(Player player, double step) {
        if (!active) return;
        windowRemaining -= step;
        if (windowRemaining <= 0) { player.setStrength(player.getStrength() / damageMultiplier); active = false; windowRemaining = 0; }
    }

    @Override public void onUnregister(Player player) {
        if (active) { player.setStrength(player.getStrength() / damageMultiplier); active = false; }
    }
}