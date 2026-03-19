package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;

/**
 * Last Stand — take 20% less damage when below 50% HP.
 * Demonstrates damage interception via onDamageTaken.
 *
 * JSON:
 *   { "type": "upgrade", "effectClass": "ma.ac.emi.gamelogic.effect.impl.LastStandEffect", ... }
 */
public class LastStandEffect implements PlayerEffect {

    private static final double HP_THRESHOLD    = 0.5;  // 50% HP
    private static final double DAMAGE_REDUCTION = 0.8; // 20% reduction

    @Override
    public double onDamageTaken(Player player, double damage) {
        if (player.getHpMax() > 0 &&
            player.getHp() / player.getHpMax() < HP_THRESHOLD) {
            return damage * DAMAGE_REDUCTION;
        }
        return damage;
    }
}
