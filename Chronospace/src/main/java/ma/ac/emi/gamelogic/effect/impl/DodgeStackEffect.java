package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;
import java.util.Map;

/** Params: dodgePerStack (default 0.003), maxStacked (default 0.08) */
public class DodgeStackEffect implements PlayerEffect {
    private double dodgePerStack = 0.003;
    private double maxStacked    = 0.08;
    private double stacked       = 0;

    @Override public void configure(Map<String, Double> p) {
        dodgePerStack = PlayerEffect.param(p, "dodgePerStack", 0.003);
        maxStacked    = PlayerEffect.param(p, "maxStacked",    0.08);
    }

    @Override public void onDodge(Player player) {
        if (stacked >= maxStacked) return;
        double gain = Math.min(dodgePerStack, maxStacked - stacked);
        stacked += gain;
        player.setDodge(player.getDodge() + gain);
    }

    @Override public void onWaveStart(Player player) {
        player.setDodge(Math.max(0, player.getDodge() - stacked)); stacked = 0;
    }

    @Override public void onUnregister(Player player) {
        player.setDodge(Math.max(0, player.getDodge() - stacked)); stacked = 0;
    }
}