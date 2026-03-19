package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;

/**
 * Nimble Synergy — dodge chance scales with movement speed.
 * For every 10% speed above base, gain +1% dodge (up to +15%).
 * Recalculates every tick — responds dynamically to other speed items.
 */
public class NimbleSynergyEffect implements PlayerEffect {

    private static final double DODGE_PER_10PCT_SPEED = 0.01;
    private static final double MAX_BONUS_DODGE       = 0.15;

    private double lastAppliedBonus = 0;

    @Override
    public void onTick(Player player, double step) {
        double speedRatio = player.getSpeed() / player.getBaseSpeed();
        double speedBonus = Math.max(0, speedRatio - 1.0);
        double bonus = Math.min(MAX_BONUS_DODGE, Math.floor(speedBonus / 0.10) * DODGE_PER_10PCT_SPEED);

        double delta = bonus - lastAppliedBonus;
        if (Math.abs(delta) > 0.0001) {
            player.setDodge(player.getDodge() + delta);
            lastAppliedBonus = bonus;
        }
    }

    @Override
    public void onUnregister(Player player) {
        player.setDodge(Math.max(0, player.getDodge() - lastAppliedBonus));
        lastAppliedBonus = 0;
    }
}
