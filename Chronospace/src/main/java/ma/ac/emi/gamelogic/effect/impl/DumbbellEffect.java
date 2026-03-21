package ma.ac.emi.gamelogic.effect.impl;

import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import java.util.Map;

public class DumbbellEffect implements PlayerEffect {

    private double hpPerStep      = 30.0;
    private double damagePerStep  = 2.0;
    private double armorPerStep   = 3.0;
    private double lastFlat       = 0;
    private double lastArmorBonus = 0;

    @Override
    public void configure(Map<String, Double> p) {
        hpPerStep     = PlayerEffect.param(p, "hpPerStep",     30.0);
        damagePerStep = PlayerEffect.param(p, "damagePerStep",  2.0);
        armorPerStep  = PlayerEffect.param(p, "armorPerStep",   3.0);
    }

    @Override
    public void onTick(Player player, double step) {
        double steps = Math.floor(player.getHpMax() / hpPerStep);

        if (player.getActiveWeapon() != null) {
            double targetFlat = steps * damagePerStep;
            if (Math.abs(targetFlat - lastFlat) > 0.001) {
                WeaponItemDefinition def = (WeaponItemDefinition)
                        player.getActiveWeapon().getWeaponItem().getItemDefinition();
                def.setDamage(def.getDamage() - lastFlat + targetFlat);
                lastFlat = targetFlat;
            }
        }

        double armorBonus = steps * armorPerStep;
        double armorDelta = armorBonus - lastArmorBonus;
        if (Math.abs(armorDelta) > 0.001) {
            player.setDefense(player.getDefense() + armorDelta);
            lastArmorBonus = armorBonus;
        }
    }

    @Override
    public void onUnregister(Player player) {
        if (player.getActiveWeapon() != null && lastFlat != 0) {
            WeaponItemDefinition def = (WeaponItemDefinition)
                    player.getActiveWeapon().getWeaponItem().getItemDefinition();
            def.setDamage(def.getDamage() - lastFlat);
        }
        player.setDefense(player.getDefense() - lastArmorBonus);
        lastFlat = 0;
        lastArmorBonus = 0;
    }

}