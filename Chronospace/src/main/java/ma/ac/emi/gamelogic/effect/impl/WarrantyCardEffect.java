package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;
import java.util.Map;

/**
 * Warranty Card — once per wave, a hit that would kill the player leaves
 * them at 1 HP instead.
 *
 * Uses onDamageApplied rather than onDamageTaken so the clamp happens
 * AFTER all other effects (damage amplifiers, Last Stand, etc.) have
 * already run — no ordering dependency, no way for another effect to
 * undo the survival.
 *
 * Params: shieldsPerWave (default 1)
 */
public class WarrantyCardEffect implements PlayerEffect {

    private int shieldsPerWave   = 1;
    private int shieldsRemaining = 0;

    @Override
    public void configure(Map<String, Double> p) {
        shieldsPerWave   = PlayerEffect.paramInt(p, "shieldsPerWave", 1);
        shieldsRemaining = shieldsPerWave;
    }

    @Override public void onRegister(Player player)  { shieldsRemaining = shieldsPerWave; }
    @Override public void onWaveStart(Player player) { shieldsRemaining = shieldsPerWave; }

    @Override
    public void onDamageApplied(Player player) {
        if (shieldsRemaining > 0 && player.getHp() <= 0) {
            shieldsRemaining--;
            player.setHp(1);
        }
    }
}