package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;
import java.util.Map;

/**
 * Dumbbell — for every N max HP, gain flat damage and flat armor.
 * Recalculates every tick. Intended to stack with BarbellEffect.
 *
 * Params: hpPerStep (default 30.0), damagePerStep (default 2.0), armorPerStep (default 3.0)
 */
public class DumbbellEffect implements PlayerEffect {

    private double hpPerStep     = 30.0;
    private double damagePerStep = 2.0;
    private double armorPerStep  = 3.0;

    private double lastDamageBonus = 0;
    private double lastArmorBonus  = 0;

    @Override
    public void configure(Map<String, Double> p) {
        hpPerStep     = PlayerEffect.param(p, "hpPerStep",     30.0);
        damagePerStep = PlayerEffect.param(p, "damagePerStep", 2.0);
        armorPerStep  = PlayerEffect.param(p, "armorPerStep",  3.0);
    }

    @Override
    public void onTick(Player player, double step) {
        double steps       = Math.floor(player.getHpMax() / hpPerStep);
        double damageBonus = steps * damagePerStep;
        double armorBonus  = steps * armorPerStep;

        double dmgDelta   = damageBonus - lastDamageBonus;
        double armorDelta = armorBonus  - lastArmorBonus;

        if (Math.abs(dmgDelta) > 0.001) {
            player.setStrength(player.getStrength() + dmgDelta);
            lastDamageBonus = damageBonus;
        }
        if (Math.abs(armorDelta) > 0.001) {
            player.setDefense(player.getDefense() + armorDelta);
            lastArmorBonus = armorBonus;
        }
    }

    @Override
    public void onUnregister(Player player) {
        player.setStrength(player.getStrength() - lastDamageBonus);
        player.setDefense(player.getDefense()   - lastArmorBonus);
        lastDamageBonus = 0;
        lastArmorBonus  = 0;
    }
}
