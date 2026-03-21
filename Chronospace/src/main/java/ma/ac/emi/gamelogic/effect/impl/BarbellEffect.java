package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import java.util.Map;

public class BarbellEffect implements PlayerEffect {

    private double hpPerStep     = 50.0;
    private double damagePerStep = 3.0;
    private double lastFlat      = 0;

    @Override
    public void configure(Map<String, Double> p) {
        hpPerStep     = PlayerEffect.param(p, "hpPerStep",     50.0);
        damagePerStep = PlayerEffect.param(p, "damagePerStep",  3.0);
    }

    @Override
    public void onTick(Player player, double step) {
        if (player.getActiveWeapon() == null) return;
        double targetFlat = Math.floor(player.getHpMax() / hpPerStep) * damagePerStep;
        if (Math.abs(targetFlat - lastFlat) < 0.001) return;

        WeaponItemDefinition def = (WeaponItemDefinition)
                player.getActiveWeapon().getWeaponItem().getItemDefinition();
        def.setDamage(def.getDamage() - lastFlat + targetFlat);
        lastFlat = targetFlat;
    }

    @Override
    public void onUnregister(Player player) {
        if (player.getActiveWeapon() == null || lastFlat == 0) return;
        WeaponItemDefinition def = (WeaponItemDefinition)
                player.getActiveWeapon().getWeaponItem().getItemDefinition();
        def.setDamage(def.getDamage() - lastFlat);
        lastFlat = 0;
    }
}