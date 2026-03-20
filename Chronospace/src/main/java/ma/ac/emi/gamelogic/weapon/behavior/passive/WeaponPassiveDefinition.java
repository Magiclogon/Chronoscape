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
    /** Returns the player stat names this passive actually modifies, for the live HUD. */
    public java.util.Set<String> getAffectedStats() {
        String name = effectName.contains(".")
                ? effectName.substring(effectName.lastIndexOf('.') + 1)
                : effectName;
        return switch (name) {
            case "MomentumPassive"    -> java.util.Set.of("strength");
            case "DodgeSynergyPassive"-> java.util.Set.of("speed");
            case "BloodRushPassive"   -> java.util.Set.of("strength");
            case "ArmorCrunchPassive" -> java.util.Set.of("strength");
            case "RegenOnKillPassive" -> java.util.Set.of("health_regen");
            default                   -> java.util.Set.of();
        };
    }

    public String describe() {
        // Extract the simple class name whether effectName is qualified or not
        String name = effectName.contains(".")
                ? effectName.substring(effectName.lastIndexOf('.') + 1)
                : effectName;
        return switch (name) {
            case "MomentumPassive" -> {
                int    hits    = param("hitsPerStack",  5);
                int    pct     = (int) Math.round(param("bonusPerStack", 0.08) * 100);
                int    max     = param("maxStacks", 6);
                yield  String.format("Every %d hits +%d%% damage (max %dx). Resets on switch", hits, pct, max);
            }
            case "DodgeSynergyPassive" -> {
                double ratio = param("speedPerDodgePct", 0.5);
                yield String.format("Each 1%% dodge grants +%.0f%% move speed", ratio * 100);
            }
            case "BloodRushPassive" -> {
                int maxPct = (int) Math.round(param("maxBonus", 0.60) * 100);
                yield String.format("Up to +%d%% damage as HP drops", maxPct);
            }
            case "ArmorCrunchPassive" -> {
                double dpa = param("damagePerArmor", 0.05);
                yield String.format("+%.2f damage per armor point", dpa);
            }
            case "RegenOnKillPassive" -> {
                double regen    = param("regenPerKill",  2.0);
                double duration = param("regenDuration", 4.0);
                double maxR     = param("maxRegen",      10.0);
                yield String.format("Kill: +%.1f HP/s for %.0fs (max %.0f HP/s)", regen, duration, maxR);
            }
            default -> effectName + " (active while equipped)";
        };
    }

    private double param(String key, double def) {
        if (params == null) return def;
        Double v = params.get(key);
        return v != null ? v : def;
    }
    private int param(String key, int def) {
        if (params == null) return def;
        Double v = params.get(key);
        return v != null ? v.intValue() : def;
    }
}