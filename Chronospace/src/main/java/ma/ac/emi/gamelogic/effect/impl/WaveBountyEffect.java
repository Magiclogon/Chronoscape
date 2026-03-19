package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.player.Player;

/**
 * Wave Bounty — tallies kills during the wave and pays out +10 gold
 * per kill when the wave ends.
 * Stackable: multiple copies each add their own payout.
 */
public class WaveBountyEffect implements PlayerEffect {

    private static final double GOLD_PER_KILL = 10.0;
    private int killCount = 0;

    @Override
    public void onKill(Player player, LivingEntity killed) {
        killCount++;
    }

    @Override
    public void onWaveEnd(Player player) {
        double bounty = killCount * GOLD_PER_KILL;
        player.setMoney(player.getMoney() + bounty);
        killCount = 0;
    }

    @Override
    public void onWaveStart(Player player) {
        killCount = 0; // reset in case wave was skipped
    }
}
