package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.player.Player;

/**
 * Crimson Belt — lifesteal scales with missing HP.
 * At full HP: 5% lifesteal. At 1 HP: up to 25% lifesteal.
 * Rewards playing on the edge.
 *
 * JSON: "effectClass": "ma.ac.emi.gamelogic.effect.impl.CrimsonBeltEffect"
 */
public class CrimsonBeltEffect implements PlayerEffect {

    private static final double MIN_RATE = 0.05;
    private static final double MAX_RATE = 0.25;

    @Override
    public void onDamageDealt(Player player, LivingEntity target, double damageDealt) {
        if (player.getHpMax() <= 0) return;
        double missingFraction = 1.0 - (player.getHp() / player.getHpMax());
        double rate = MIN_RATE + missingFraction * (MAX_RATE - MIN_RATE);
        double heal = damageDealt * rate;
        player.setHp(Math.min(player.getHp() + heal, player.getHpMax()));
    }
}
