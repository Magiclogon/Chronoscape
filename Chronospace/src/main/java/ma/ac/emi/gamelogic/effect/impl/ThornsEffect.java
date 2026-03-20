package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.player.Player;
import java.util.Map;


public class ThornsEffect implements PlayerEffect {

    private double reduction   = 0.08;
    private double reflectRate = 0.25;

    @Override
    public void configure(Map<String, Double> p) {
        reduction   = PlayerEffect.param(p, "reduction",   0.08);
        reflectRate = PlayerEffect.param(p, "reflectRate", 0.25);
    }

    @Override
    public double onDamageTaken(Player player, double damage, LivingEntity attacker) {
        if (attacker != null && !attacker.isDead() && attacker.getHp() > 0) {
            double reflected = damage * reflectRate;
            attacker.setHp(Math.max(0, attacker.getHp() - reflected));
            attacker.onHit();
        }
        return damage * (1.0 - reduction);
    }
}