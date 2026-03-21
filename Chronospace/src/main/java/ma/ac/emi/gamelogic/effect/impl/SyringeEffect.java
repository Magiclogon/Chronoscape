package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.player.Player;
import java.util.Map;

/**
 * Syringe — on kill, heal flat HP plus a percentage of the enemy's max HP.
 * Params: flatHeal (default 8.0), enemyHpPercent (default 0.05)
 */
public class SyringeEffect implements PlayerEffect {

    private double flatHeal        = 8.0;
    private double enemyHpPercent  = 0.05;

    @Override
    public void configure(Map<String, Double> p) {
        flatHeal       = PlayerEffect.param(p, "flatHeal",       8.0);
        enemyHpPercent = PlayerEffect.param(p, "enemyHpPercent", 0.05);
    }

    @Override
    public void onKill(Player player, LivingEntity killed) {
        player.heal(flatHeal + killed.getHpMax() * enemyHpPercent);
    }
}