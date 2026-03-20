package ma.ac.emi.gamelogic.weapon.behavior.passive;

import ma.ac.emi.gamelogic.weapon.behavior.WeaponBehavior;
import ma.ac.emi.gamelogic.weapon.behavior.WeaponBehaviorDefinition;

/**
 * Definition for a PassiveWeaponEffect — created by WeaponBehaviorFactory
 * when it reads "type": "passive" from the weapon's behaviors array.
 *
 * ── Add to WeaponBehaviorFactory.create() ────────────────────────────────────
 *
 *   case "passive" -> {
 *       String stat      = behaviorJson.get("stat").getAsString();
 *       double value     = behaviorJson.get("value").getAsDouble();
 *       String opStr     = behaviorJson.has("operation")
 *                          ? behaviorJson.get("operation").getAsString().toUpperCase()
 *                          : "ADD";
 *       PassiveWeaponEffect.Operation op =
 *           PassiveWeaponEffect.Operation.valueOf(opStr);
 *       yield new PassiveWeaponEffectDefinition(stat, value, op);
 *   }
 *
 * ─────────────────────────────────────────────────────────────────────────────
 */
public class PassiveWeaponEffectDefinition extends WeaponBehaviorDefinition {

    private final String                      stat;
    private final double                      value;
    private final PassiveWeaponEffect.Operation operation;

    public PassiveWeaponEffectDefinition(String stat, double value,
                                          PassiveWeaponEffect.Operation operation) {
        super(0, 0);
        this.stat      = stat;
        this.value     = value;
        this.operation = operation;
    }

    public String getStat() { return stat; }

    @Override
    public WeaponBehavior create() {
        return new PassiveWeaponEffect(stat, value, operation);
    }

    /** Human-readable summary for the item description panel. */
    public String describe() {
        String statLabel = switch (stat.toLowerCase()) {
            case "dodge"        -> "Dodge";
            case "defense"      -> "Armor";
            case "speed"        -> "Move Speed";
            case "strength"     -> "Damage";
            case "health_regen" -> "HP Regen";
            default             -> stat;
        };
        String valueStr = operation == PassiveWeaponEffect.Operation.ADD
                ? (value >= 0 ? String.format("+%.2f", value) : String.format("%.2f", value))
                : (value >= 1 ? String.format("+%.2f%%", (value - 1) * 100)
                              : String.format("%.2f%%", (value - 1) * 100));
        return statLabel + " " + valueStr + " while equipped";
    }
}