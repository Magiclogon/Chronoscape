package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.player.Player;
import java.util.Map;

/**
 * Wave Bounty — pays out gold per kill at wave end.
 * Params: goldPerKill (default 10.0)
 */
public class WaveBountyEffect implements PlayerEffect {

    private double goldPerKill = 10.0;
    private int    killCount   = 0;

    @Override
    public void configure(Map<String, Double> p) {
        goldPerKill = PlayerEffect.param(p, "goldPerKill", 10.0);
    }

    @Override
    public void onKill(Player player, LivingEntity killed) { killCount++; }

    @Override
    public void onWaveEnd(Player player) {
        player.setMoney(player.getMoney() + killCount * goldPerKill);
        killCount = 0;
    }

    @Override
    public void onWaveStart(Player player) { killCount = 0; }
}