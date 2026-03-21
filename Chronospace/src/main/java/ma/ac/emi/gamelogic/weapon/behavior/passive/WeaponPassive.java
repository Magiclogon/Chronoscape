package ma.ac.emi.gamelogic.weapon.behavior.passive;

import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.gamelogic.weapon.behavior.WeaponBehavior;

import java.util.Map;

/**
 * Base class for complex weapon passives that go beyond a single stat ADD/MULTIPLY.
 * Subclass this and implement the hooks you need. All hooks are no-ops by default.
 *
 * Registered JSON:
 *   { "type": "passive_complex", "effect": "MomentumPassive", "params": { ... } }
 *
 * Add to WeaponBehaviorFactory.create():
 *   case "passive_complex" -> {
 *       String effect = behaviorJson.get("effect").getAsString();
 *       Map<String, Double> params = parseParams(behaviorJson);
 *       yield new WeaponPassiveDefinition(effect, params);
 *   }
 */
public abstract class WeaponPassive extends WeaponBehavior {

    protected Map<String, Double> params;

    public WeaponPassive() { super(0, 0); }

    /** Called once after construction to pass JSON params. */
    public void configure(Map<String, Double> params) { this.params = params; }

    protected double param(String key, double def) {
        if (params == null) return def;
        Double v = params.get(key);
        return v != null ? v : def;
    }
    protected int paramInt(String key, int def) {
        if (params == null) return def;
        Double v = params.get(key);
        return v != null ? v.intValue() : def;
    }

    /** Convenience — extracts the Player bearer, returns null if not a player weapon. */
    protected Player player(Weapon weapon) {
        return weapon.getBearer() instanceof Player p ? p : null;
    }

    /** Human-readable description of what this passive does, shown in item tooltip. */
    public abstract String describe();

    // ── Default no-ops so subclasses only override what they need ─────────
    @Override public void onInit(Weapon weapon)                {}
    @Override public void onUpdate(Weapon weapon, double step)  {}
    @Override public void onAttack(Weapon weapon, double step)  {}
    @Override public void onSwitchIn(Weapon weapon)             {}
    @Override public void onSwitchOut(Weapon weapon)            {}
    @Override public void onWaveStart(Weapon weapon)            {}
    @Override public void onWaveEnd(Weapon weapon)              {}
}