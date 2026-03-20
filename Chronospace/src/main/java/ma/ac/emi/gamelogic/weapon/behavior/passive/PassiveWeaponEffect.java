package ma.ac.emi.gamelogic.weapon.behavior.passive;

import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.gamelogic.weapon.behavior.WeaponBehavior;

/**
 * Applies a stat bonus to the player while this weapon is the active weapon.
 * Bonus activates on switchIn, deactivates on switchOut.
 *
 * Supported stats (case-insensitive):
 *   dodge, defense, speed, strength, health_regen
 *
 * JSON example:
 *   {
 *     "type": "passive",
 *     "stat": "dodge",
 *     "value": 0.15,
 *     "operation": "ADD"       <- ADD or MULTIPLY
 *   }
 */
public class PassiveWeaponEffect extends WeaponBehavior {

    public enum Operation { ADD, MULTIPLY }

    private final String    stat;
    private final double    value;
    private final Operation operation;

    private boolean active = false;

    public PassiveWeaponEffect(String stat, double value, Operation operation) {
        super(0, 0);
        this.stat      = stat.toLowerCase();
        this.value     = value;
        this.operation = operation;
    }

    // ── WeaponBehavior hooks ──────────────────────────────────────────────

    @Override public void onInit(Weapon weapon)               {}
    @Override public void onUpdate(Weapon weapon, double step) {}
    @Override public void onAttack(Weapon weapon, double step) {}

    @Override
    public void onSwitchIn(Weapon weapon) {
        if (active) return;
        Player player = getPlayer(weapon);
        if (player == null) return;
        apply(player, true);
        active = true;
    }

    @Override
    public void onSwitchOut(Weapon weapon) {
        if (!active) return;
        Player player = getPlayer(weapon);
        if (player == null) return;
        apply(player, false);
        active = false;
    }

    // ── Stat application ──────────────────────────────────────────────────

    private void apply(Player player, boolean adding) {
        switch (stat) {
            case "dodge" -> {
                double delta = operation == Operation.ADD
                        ? (adding ? value : -value)
                        : (adding ? player.getDodge() * (value - 1) : -player.getDodge() * (1 - 1.0 / value));
                player.setDodge(player.getDodge() + delta);
            }
            case "defense" -> {
                double delta = operation == Operation.ADD
                        ? (adding ? value : -value)
                        : (adding ? player.getDefense() * (value - 1) : -player.getDefense() * (1 - 1.0 / value));
                player.setDefense(player.getDefense() + delta);
            }
            case "speed" -> {
                if (operation == Operation.MULTIPLY) {
                    player.setSpeed(adding ? player.getSpeed() * value : player.getSpeed() / value);
                } else {
                    player.setSpeed(player.getSpeed() + (adding ? value : -value));
                }
            }
            case "strength" -> {
                if (operation == Operation.MULTIPLY) {
                    player.setStrength(adding ? player.getStrength() * value : player.getStrength() / value);
                } else {
                    player.setStrength(player.getStrength() + (adding ? value : -value));
                }
            }
            case "health_regen" -> {
                player.setRegenerationSpeed(player.getRegenerationSpeed() + (adding ? value : -value));
            }
            default -> System.err.println("PassiveWeaponEffect: unknown stat '" + stat + "'");
        }
    }

    private Player getPlayer(Weapon weapon) {
        if (weapon.getBearer() instanceof Player p) return p;
        return null; // enemies don't benefit from passive effects
    }
}