package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import java.util.Map;

public class BerserkerSoulEffect implements PlayerEffect {

    private double bonusPerKill    = 0.005;
    private double maxBonus        = 0.20;
    private double currentBonus    = 0;
    private double lastMultiplier  = 1.0;

    @Override
    public void configure(Map<String, Double> p) {
        bonusPerKill = PlayerEffect.param(p, "bonusPerKill", 0.005);
        maxBonus     = PlayerEffect.param(p, "maxBonus",     0.20);
    }

    @Override
    public void onKill(Player player, LivingEntity killed) {
        currentBonus = Math.min(maxBonus, currentBonus + bonusPerKill);
    }

    @Override
    public void onTick(Player player, double step) {
        if (player.getActiveWeapon() == null) return;
        double targetMultiplier = 1.0 + currentBonus;
        if (Math.abs(targetMultiplier - lastMultiplier) < 0.0001) return;

        WeaponItemDefinition def = (WeaponItemDefinition)
                player.getActiveWeapon().getWeaponItem().getItemDefinition();
        def.setDamage(def.getDamage() / lastMultiplier * targetMultiplier);
        lastMultiplier = targetMultiplier;
    }

    @Override
    public void onWaveStart(Player player) {
        if (player.getActiveWeapon() != null && lastMultiplier != 1.0) {
            WeaponItemDefinition def = (WeaponItemDefinition)
                    player.getActiveWeapon().getWeaponItem().getItemDefinition();
            def.setDamage(def.getDamage() / lastMultiplier);
        }
        currentBonus   = 0;
        lastMultiplier = 1.0;
    }

    @Override
    public void onWaveEnd(Player player) {
        if (player.getActiveWeapon() != null && lastMultiplier != 1.0) {
            WeaponItemDefinition def = (WeaponItemDefinition)
                    player.getActiveWeapon().getWeaponItem().getItemDefinition();
            def.setDamage(def.getDamage() / lastMultiplier);
        }
        currentBonus   = 0;
        lastMultiplier = 1.0;
    }
}