package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;
import java.util.Map;

/**
 * Overflow Flask — when a heal overflows beyond max HP, the excess
 * permanently increases max HP with diminishing returns.
 *
 * The conversion rate scales inversely with how much max HP the player
 * already has relative to their base max HP:
 *
 *   bonus = overflow * conversionRate * (baseHpMax / currentHpMax)
 *
 * Examples with conversionRate=0.5 and baseHpMax=100:
 *   At 100 max HP  → rate = 0.50  (10 overflow → +5 max HP)
 *   At 200 max HP  → rate = 0.25  (10 overflow → +2.5 max HP)
 *   At 400 max HP  → rate = 0.125 (10 overflow → +1.25 max HP)
 *
 * It never hard-caps — it just asymptotically slows down. Large pickups
 * still feel rewarding, and there's always a reason to collect more.
 *
 * Params:
 *   conversionRate (default 0.5) — fraction of overflow converted at baseline HP
 */
public class OverhealEffect implements PlayerEffect {

    private double conversionRate = 0.5;
    private double totalGained    = 0;
    private double baseHpMax      = -1; // captured on first heal

    @Override
    public void configure(Map<String, Double> p) {
        conversionRate = PlayerEffect.param(p, "conversionRate", 0.5);
    }

    @Override
    public void onHeal(Player player, double requested, double gained) {
        double overflow = requested - gained;
        if (overflow <= 0) return;

        // Capture the player's base max HP the first time we fire
        if (baseHpMax < 0) baseHpMax = player.getHpMax();

        // Diminishing returns: rate falls as hpMax grows above baseline
        double rate  = conversionRate * (baseHpMax / player.getHpMax());
        double bonus = overflow * rate;
        if (bonus < 0.01) return; // skip negligible amounts

        totalGained += bonus;
        player.setHpMax(player.getHpMax() + bonus);
        player.setHp(player.getHpMax()); // top up to new max
    }

    @Override
    public void onUnregister(Player player) {
        player.setHpMax(Math.max(1, player.getHpMax() - totalGained));
        player.setHp(Math.min(player.getHp(), player.getHpMax()));
        totalGained = 0;
        baseHpMax   = -1;
    }
}