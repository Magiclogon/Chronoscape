package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;

/**
 * Phantom Step — after dodging, gain +60% speed for 2 seconds.
 * Encourages aggressive movement after a dodge.
 */
public class PhantomStepEffect implements PlayerEffect {

    private static final double BOOST_FACTOR   = 1.6;
    private static final double BOOST_DURATION = 2.0;

    private double boostRemaining = 0;
    private boolean boosted = false;

    @Override
    public void onDodge(Player player) {
        if (!boosted) {
            player.setSpeed(player.getSpeed() * BOOST_FACTOR);
            boosted = true;
        }
        boostRemaining = BOOST_DURATION; // refresh on each dodge
    }

    @Override
    public void onTick(Player player, double step) {
        if (!boosted) return;
        boostRemaining -= step;
        if (boostRemaining <= 0) {
            player.setSpeed(player.getSpeed() / BOOST_FACTOR);
            boosted = false;
            boostRemaining = 0;
        }
    }

    @Override
    public void onUnregister(Player player) {
        if (boosted) {
            player.setSpeed(player.getSpeed() / BOOST_FACTOR);
            boosted = false;
        }
    }
}
