package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;

/**
 * Paper Skin — for every 10% speed above base speed, incoming damage
 * is increased by 3%. Fast players get punished harder.
 *
 * Example: base speed 200, current speed 280 (+40%) → +12% more damage taken.
 */
public class PaperSkinEffect implements PlayerEffect {

    @Override
    public double onDamageTaken(Player player, double damage) {
        double speedRatio = player.getSpeed() / player.getBaseSpeed();
        if (speedRatio <= 1.0) return damage;

        // +3% per 10% speed above base
        double speedBonusTenths = Math.floor((speedRatio - 1.0) / 0.10);
        double multiplier = 1.0 + (speedBonusTenths * 0.03);
        return damage * multiplier;
    }
}
