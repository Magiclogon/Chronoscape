package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import java.util.Map;

public class CounterStrikeEffect implements PlayerEffect {

    private double damageBonus     = 0.8;
    private double windowDuration  = 1.0;
    private double windowRemaining = 0;
    private double lastMultiplier  = 1.0;

    @Override
    public void configure(Map<String, Double> p) {
        damageBonus    = PlayerEffect.param(p, "damageMultiplier", 1.8) - 1.0;
        windowDuration = PlayerEffect.param(p, "windowDuration", 1.0);
    }

    @Override
    public void onDodge(Player player) {
        windowRemaining = windowDuration;
    }

    @Override
    public void onTick(Player player, double step) {
        if (player.getActiveWeapon() == null) return;
        boolean wasActive = windowRemaining > 0;
        windowRemaining = Math.max(0, windowRemaining - step);
        boolean isActive = windowRemaining > 0;

        double targetMultiplier = isActive ? (1.0 + damageBonus) : 1.0;
        if (Math.abs(targetMultiplier - lastMultiplier) < 0.0001) return;

        WeaponItemDefinition def = (WeaponItemDefinition)
                player.getActiveWeapon().getWeaponItem().getItemDefinition();
        def.setDamage(def.getDamage() / lastMultiplier * targetMultiplier);
        lastMultiplier = targetMultiplier;
    }

    @Override
    public void onWaveEnd(Player player) {
        windowRemaining = 0;
        if (player.getActiveWeapon() == null || lastMultiplier == 1.0) return;
        WeaponItemDefinition def = (WeaponItemDefinition)
                player.getActiveWeapon().getWeaponItem().getItemDefinition();
        def.setDamage(def.getDamage() / lastMultiplier);
        lastMultiplier = 1.0;
    }
}