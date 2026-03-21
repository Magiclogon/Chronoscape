package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import java.util.Map;

public class KillingSpreeEffect implements PlayerEffect {

    private double damagePerKill    = 1.5;
    private double accumulatedBonus = 0;
    private double lastApplied      = 0;

    @Override
    public void configure(Map<String, Double> p) {
        damagePerKill = PlayerEffect.param(p, "damagePerKill", 1.5);
    }

    @Override
    public void onKill(Player player, LivingEntity killed) {
        accumulatedBonus += damagePerKill;
    }

    @Override
    public void onTick(Player player, double step) {
        if (player.getActiveWeapon() == null) return;
        double target = accumulatedBonus;
        if (Math.abs(target - lastApplied) < 0.001) return;

        WeaponItemDefinition def = (WeaponItemDefinition)
                player.getActiveWeapon().getWeaponItem().getItemDefinition();
        def.setDamage(def.getDamage() - lastApplied + target);
        lastApplied = target;
    }

    @Override
    public void onWaveStart(Player player) {
        if (player.getActiveWeapon() != null && lastApplied != 0) {
            WeaponItemDefinition def = (WeaponItemDefinition)
                    player.getActiveWeapon().getWeaponItem().getItemDefinition();
            def.setDamage(def.getDamage() - lastApplied);
        }
        accumulatedBonus = 0;
        lastApplied      = 0;
    }

    @Override
    public void onWaveEnd(Player player) {
        if (player.getActiveWeapon() != null && lastApplied != 0) {
            WeaponItemDefinition def = (WeaponItemDefinition)
                    player.getActiveWeapon().getWeaponItem().getItemDefinition();
            def.setDamage(def.getDamage() - lastApplied);
        }
        accumulatedBonus = 0;
        lastApplied      = 0;
    }
}