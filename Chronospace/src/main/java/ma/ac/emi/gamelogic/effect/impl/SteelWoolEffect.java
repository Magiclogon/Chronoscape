package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import java.util.Map;

public class SteelWoolEffect implements PlayerEffect {

    private double armorPerStep    = 20.0;
    private double bonusPerStep    = 0.03;
    private double lastMultiplier  = 1.0;

    @Override
    public void configure(Map<String, Double> p) {
        armorPerStep = PlayerEffect.param(p, "armorPerStep",      20.0);
        bonusPerStep = PlayerEffect.param(p, "attackSpeedPerStep", 0.03);
    }

    @Override
    public void onTick(Player player, double step) {
        if (player.getActiveWeapon() == null) return;
        double targetMultiplier = 1.0 + Math.floor(player.getDefense() / armorPerStep) * bonusPerStep;
        if (Math.abs(targetMultiplier - lastMultiplier) < 0.0001) return;

        WeaponItemDefinition def = (WeaponItemDefinition)
                player.getActiveWeapon().getWeaponItem().getItemDefinition();
        def.setDamage(def.getDamage() / lastMultiplier * targetMultiplier);
        lastMultiplier = targetMultiplier;
    }

    @Override
    public void onUnregister(Player player) {
        if (player.getActiveWeapon() == null || lastMultiplier == 1.0) return;
        WeaponItemDefinition def = (WeaponItemDefinition)
                player.getActiveWeapon().getWeaponItem().getItemDefinition();
        def.setDamage(def.getDamage() / lastMultiplier);
        lastMultiplier = 1.0;
    }
}