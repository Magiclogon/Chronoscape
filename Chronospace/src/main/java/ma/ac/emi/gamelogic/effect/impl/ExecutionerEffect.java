package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.player.Player;
import java.util.Map;

/**
 * Executioner — every N kills instantly restores flat HP.
 * Params: killsRequired (default 10), healAmount (default 20.0)
 */
public class ExecutionerEffect implements PlayerEffect {

    private int    killsRequired = 10;
    private double healAmount    = 20.0;
    private int    killCount     = 0;

    @Override
    public void configure(Map<String, Double> p) {
        killsRequired = PlayerEffect.paramInt(p, "killsRequired", 10);
        healAmount    = PlayerEffect.param(p,    "healAmount",    20.0);
    }

    @Override
    public void onKill(Player player, LivingEntity killed) {
        if (++killCount % killsRequired == 0)
            player.heal(healAmount);
    }
}