package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;

/**
 * Four-Leaf Clover — each dodge heals 8 HP.
 * Simple synergy: more dodge chance = more healing.
 */
public class CloverEffect implements PlayerEffect {

    private static final double HEAL_PER_DODGE = 8.0;

    @Override
    public void onDodge(Player player) {
        player.setHp(Math.min(player.getHp() + HEAL_PER_DODGE, player.getHpMax()));
    }
}
