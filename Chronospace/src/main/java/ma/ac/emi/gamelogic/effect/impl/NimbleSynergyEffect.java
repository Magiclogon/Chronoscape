package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;
import java.util.Map;

/** Params: dodgePer10PctSpeed (default 0.01), maxBonusDodge (default 0.10) */
public class NimbleSynergyEffect implements PlayerEffect {
    private double dodgePer10PctSpeed = 0.01;
    private double maxBonusDodge      = 0.10;
    private double lastAppliedBonus   = 0;

    @Override public void configure(Map<String, Double> p) {
        dodgePer10PctSpeed = PlayerEffect.param(p, "dodgePer10PctSpeed", 0.01);
        maxBonusDodge      = PlayerEffect.param(p, "maxBonusDodge",      0.10);
    }

    @Override public void onTick(Player player, double step) {
        double speedBonus = Math.max(0, player.getSpeed() / player.getBaseSpeed() - 1.0);
        double bonus = Math.min(maxBonusDodge, Math.floor(speedBonus / 0.10) * dodgePer10PctSpeed);
        double delta = bonus - lastAppliedBonus;
        if (Math.abs(delta) > 0.0001) { player.setDodge(player.getDodge() + delta); lastAppliedBonus = bonus; }
    }

    @Override public void onUnregister(Player player) {
        player.setDodge(Math.max(0, player.getDodge() - lastAppliedBonus)); lastAppliedBonus = 0;
    }
}