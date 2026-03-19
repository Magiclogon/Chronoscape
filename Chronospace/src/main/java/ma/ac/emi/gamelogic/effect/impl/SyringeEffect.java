package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.player.Player;

/**
 * Syringe — every kill heals a flat 8 HP plus 5% of the killed enemy's max HP.
 * Scales with enemy strength rather than player damage output.
 *
 * JSON: "effectClass": "ma.ac.emi.gamelogic.effect.impl.SyringeEffect"
 */
public class SyringeEffect implements PlayerEffect {

    private static final double FLAT_HEAL        = 8.0;
    private static final double ENEMY_HP_PERCENT = 0.05;

    @Override
    public void onKill(Player player, LivingEntity killed) {
        double heal = FLAT_HEAL + killed.getHpMax() * ENEMY_HP_PERCENT;
        player.setHp(Math.min(player.getHp() + heal, player.getHpMax()));
    }
}
