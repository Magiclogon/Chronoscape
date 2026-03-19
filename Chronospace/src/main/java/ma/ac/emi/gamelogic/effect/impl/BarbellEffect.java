package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;

/**
 * Barbell — for every 50 max HP the player has, grant +5 flat weapon damage.
 * Recalculates every tick so it automatically responds to other HP-boosting items.
 */
public class BarbellEffect implements PlayerEffect {

    private static final double HP_PER_STEP     = 50.0;
    private static final double DAMAGE_PER_STEP = 5.0;

    private double lastAppliedBonus = 0;

    @Override
    public void onTick(Player player, double step) {
        double bonus = Math.floor(player.getHpMax() / HP_PER_STEP) * DAMAGE_PER_STEP;
        double delta = bonus - lastAppliedBonus;
        if (Math.abs(delta) > 0.001) {
            player.setStrength(player.getStrength() + delta);
            lastAppliedBonus = bonus;
        }
    }

    @Override
    public void onUnregister(Player player) {
        player.setStrength(player.getStrength() - lastAppliedBonus);
        lastAppliedBonus = 0;
    }
}
