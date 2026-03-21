package ma.ac.emi.gamelogic.weapon.behavior.passive;

import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.gamelogic.weapon.Weapon;

/**
 * Momentum — consecutive hits stack a multiplicative damage bonus. Resets on switch.
 * Uses delta tracking so it doesn't interfere with item upgrade bonuses.
 * Params: hitsPerStack (default 5), bonusPerStack (default 0.08), maxStacks (default 6)
 */
public class MomentumPassive extends WeaponPassive {

    private int    hitsPerStack  = 5;
    private double bonusPerStack = 0.08;
    private int    maxStacks     = 6;

    private int    hitCount       = 0;
    private int    currentStacks  = 0;
    private double lastMultiplier = 1.0; // the multiplier we last applied

    @Override
    public void configure(java.util.Map<String, Double> p) {
        super.configure(p);
        hitsPerStack  = paramInt("hitsPerStack",  5);
        bonusPerStack = param("bonusPerStack", 0.08);
        maxStacks     = paramInt("maxStacks",     6);
    }

    @Override
    public void onSwitchIn(Weapon weapon) {
        // Weapon definition was just reset by recalculateAllUpgrades/initWeapons —
        // lastMultiplier is stale, so just zero everything out without undoing.
        hitCount       = 0;
        currentStacks  = 0;
        lastMultiplier = 1.0;
    }

    @Override
    public void onSwitchOut(Weapon weapon) {
        // Undo last applied multiplier so the weapon def is clean for other effects
        if (lastMultiplier != 1.0) {
            WeaponItemDefinition def = (WeaponItemDefinition) weapon.getWeaponItem().getItemDefinition();
            def.setDamage(def.getDamage() / lastMultiplier);
        }
        hitCount       = 0;
        currentStacks  = 0;
        lastMultiplier = 1.0;
    }

    @Override
    public void onWaveEnd(Weapon weapon) {
        // Undo the damage bonus before the shop's recalculateAllUpgrades resets
        // the weapon definition — prevents the inflated value from being carried over
        if (lastMultiplier != 1.0) {
            WeaponItemDefinition def = (WeaponItemDefinition) weapon.getWeaponItem().getItemDefinition();
            def.setDamage(def.getDamage() / lastMultiplier);
        }
        hitCount       = 0;
        currentStacks  = 0;
        lastMultiplier = 1.0;
    }

    @Override
    public void onAttack(Weapon weapon, double step) {
        if (currentStacks >= maxStacks) return;
        if (++hitCount % hitsPerStack == 0) currentStacks++;
    }

    @Override
    public void onUpdate(Weapon weapon, double step) {
        double targetMultiplier = 1.0 + currentStacks * bonusPerStack;
        if (Math.abs(targetMultiplier - lastMultiplier) < 0.0001) return;

        WeaponItemDefinition def = (WeaponItemDefinition) weapon.getWeaponItem().getItemDefinition();
        // Undo previous multiplier, apply new one — preserves whatever else is in the damage value
        def.setDamage(def.getDamage() / lastMultiplier * targetMultiplier);
        lastMultiplier = targetMultiplier;
    }
    
    @Override
    public String describe() {
        int    hits = paramInt("hitsPerStack",  5);
        int    pct  = (int) Math.round(param("bonusPerStack", 0.08) * 100);
        int    max  = paramInt("maxStacks",     6);
        return String.format("Every %d hits +%d%% damage (max %dx). Resets on switch", hits, pct, max);
    }
}