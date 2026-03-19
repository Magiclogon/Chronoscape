package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.player.Player;

/**
 * Berserker Soul — each kill stacks +1% damage bonus up to +50% total.
 * Works alongside the base +30% MULTIPLY modifier in the JSON.
 */
public class BerserkerSoulEffect implements PlayerEffect {

    private static final double BONUS_PER_KILL  = 0.01; // 1%
    private static final double MAX_BONUS        = 0.50; // 50% cap
    private double currentBonus = 0;

    @Override
    public void onKill(Player player, LivingEntity killed) {
        if (currentBonus >= MAX_BONUS) return;
        double added = Math.min(BONUS_PER_KILL, MAX_BONUS - currentBonus);
        currentBonus += added;
        player.setStrength(player.getStrength() * (1.0 + added));
    }

    @Override
    public void onUnregister(Player player) {
        if (currentBonus > 0) {
            player.setStrength(player.getStrength() / (1.0 + currentBonus));
            currentBonus = 0;
        }
    }

    @Override
    public void onWaveStart(Player player) {
        // Reset kill stacks each wave — keeps the item from snowballing permanently
        if (currentBonus > 0) {
            player.setStrength(player.getStrength() / (1.0 + currentBonus));
            currentBonus = 0;
        }
    }
}
