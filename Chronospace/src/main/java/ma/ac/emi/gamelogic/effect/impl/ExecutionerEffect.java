package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.player.Player;

/**
 * Executioner — every 10th kill instantly restores 20 HP.
 */
public class ExecutionerEffect implements PlayerEffect {

    private int    killsRequired = 10;
    private double healAmount    = 20.0;

    private int killCount = 0;

    @Override
    public void onKill(Player player, LivingEntity killed) {
        if (++killCount % killsRequired == 0) {
            player.setHp(Math.min(player.getHp() + healAmount, player.getHpMax()));
        }
    }
}
