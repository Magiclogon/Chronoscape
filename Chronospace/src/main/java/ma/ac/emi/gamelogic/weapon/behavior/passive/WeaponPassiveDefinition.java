package ma.ac.emi.gamelogic.weapon.behavior.passive;

import ma.ac.emi.gamelogic.weapon.behavior.WeaponBehavior;
import ma.ac.emi.gamelogic.weapon.behavior.WeaponBehaviorDefinition;

import java.util.Map;

/**
 * Instantiates a WeaponPassive by class name via reflection.
 * The class name is resolved relative to this package if unqualified.
 *
 * ── Add to WeaponBehaviorFactory.create() ────────────────────────────────────
 *
 *   case "passive_complex" -> {
 *       String effect = behaviorJson.get("effect").getAsString();
 *       Map<String, Double> params = new java.util.HashMap<>();
 *       if (behaviorJson.has("params")) {
 *           behaviorJson.getAsJsonObject("params").entrySet()
 *               .forEach(e -> params.put(e.getKey(), e.getValue().getAsDouble()));
 *       }
 *       yield new WeaponPassiveDefinition(effect, params);
 *   }
 *
 * ─────────────────────────────────────────────────────────────────────────────
 */
public class WeaponPassiveDefinition extends WeaponBehaviorDefinition {

    private static final String PACKAGE =
            "ma.ac.emi.gamelogic.weapon.behavior.passive.";

    private final String              effectName;
    private final Map<String, Double> params;

    public WeaponPassiveDefinition(String effectName, Map<String, Double> params) {
        super(0, 0);
        this.effectName = effectName;
        this.params     = params;
    }

    @Override
    public WeaponBehavior create() {
        String fqn = effectName.contains(".") ? effectName : PACKAGE + effectName;
        try {
            Class<?> cls = Class.forName(fqn);
            WeaponPassive passive = (WeaponPassive) cls.getDeclaredConstructor().newInstance();
            passive.configure(params);
            return passive;
        } catch (Exception e) {
            System.err.println("WeaponPassiveDefinition: could not instantiate '"
                    + fqn + "': " + e.getMessage());
            return null;
        }
    }

    /** Human-readable summary for the item description panel. */
    /** Returns the player/weapon stat names this passive actually modifies, for the live HUD. */
    public java.util.Set<String> getAffectedStats() {
        String name = effectName.contains(".")
                ? effectName.substring(effectName.lastIndexOf('.') + 1)
                : effectName;
        return switch (name) {
            case "MomentumPassive"    -> java.util.Set.of("strength"); // damage via effectDamageMul
            case "DodgeSynergyPassive"-> java.util.Set.of("speed");
            case "BloodRushPassive"   -> java.util.Set.of("strength"); // damage via effectDamageMul
            case "ArmorCrunchPassive" -> java.util.Set.of("strength"); // damage via effectDamageFlat
            case "RegenOnKillPassive" -> java.util.Set.of("health_regen");
            case "BulletHosePassive"  -> java.util.Set.of("magazine");
            default                   -> java.util.Set.of();
        };
    }

    public String describe() {
        // Delegate to the passive instance — each WeaponPassive describes itself
        String fqn = effectName.contains(".") ? effectName : PACKAGE + effectName;
        try {
            Class<?> cls     = Class.forName(fqn);
            WeaponPassive passive = (WeaponPassive) cls.getDeclaredConstructor().newInstance();
            passive.configure(params);
            return passive.describe();
        } catch (Exception e) {
            return effectName + " (active while equipped)";
        }
    }
}