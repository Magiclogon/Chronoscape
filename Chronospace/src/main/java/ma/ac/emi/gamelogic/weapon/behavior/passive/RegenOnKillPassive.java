package ma.ac.emi.gamelogic.weapon.behavior.passive;

import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.weapon.Weapon;

/**
 * Regen on Kill — while equipped, killing an enemy temporarily boosts HP regen.
 * The bonus stacks on repeated kills and fades over time.
 * Designed for: Flamethrower (wave-clear weapon that sustains through fights)
 *
 * Params:
 *   regenPerKill  (default 2.0)  — HP/s added per kill
 *   regenDuration (default 4.0)  — seconds each kill stack lasts
 *   maxRegen      (default 10.0) — cap on total bonus regen
 */
public class RegenOnKillPassive extends WeaponPassive {

    private double regenPerKill  = 2.0;
    private double regenDuration = 4.0;
    private double maxRegen      = 10.0;

    private double activeBonus    = 0;
    private double timeRemaining  = 0;
    private boolean active        = false;

    @Override
    public void configure(java.util.Map<String, Double> p) {
        super.configure(p);
        regenPerKill  = param("regenPerKill",  2.0);
        regenDuration = param("regenDuration", 4.0);
        maxRegen      = param("maxRegen",      10.0);
    }

    @Override public void onSwitchIn(Weapon weapon)  { active = true; }

    @Override
    public void onSwitchOut(Weapon weapon) {
        Player player = player(weapon);
        if (player != null && activeBonus > 0)
            player.setRegenerationSpeed(player.getRegenerationSpeed() - activeBonus);
        activeBonus = 0;
        timeRemaining = 0;
        active = false;
    }

    // Called from Ennemy.update() kill detection — we hook via onAttack since
    // WeaponBehavior doesn't have onKill. Instead, track it through the
    // EffectContext's fireOnKill if available, or approximate via onUpdate checking
    // that enemy HP dropped to 0 this frame. Simpler: expose via the weapon bearer's
    // inventory effectContext kill hook by wiring a PlayerEffect alongside.
    //
    // For now: the passive listens for kills via a companion VampirismEffect-like
    // approach — add a "kill_listener" effectClass alongside the passive in JSON.
    // Alternatively add onKill to WeaponBehavior (recommended, see note below).
    //
    // ── Recommended: add to WeaponBehavior ───────────────────────────────────
    // public void onKill(Weapon weapon, LivingEntity killed) {}
    // Call from Ennemy.update() where fireOnKill is already called:
    //   if (player.getActiveWeapon() != null)
    //     player.getActiveWeapon().getBehaviors().forEach(b -> b.onKill(weapon, this));
    // ─────────────────────────────────────────────────────────────────────────

    public void onKill(Weapon weapon, LivingEntity killed) {
        if (!active) return;
        Player player = player(weapon);
        if (player == null) return;

        double gain = Math.min(regenPerKill, maxRegen - activeBonus);
        if (gain > 0) {
            player.setRegenerationSpeed(player.getRegenerationSpeed() + gain);
            activeBonus += gain;
        }
        timeRemaining = regenDuration; // refresh timer on each kill
    }

    @Override
    public void onUpdate(Weapon weapon, double step) {
        if (!active || activeBonus <= 0) return;
        Player player = player(weapon);
        if (player == null) return;

        timeRemaining -= step;
        if (timeRemaining <= 0) {
            player.setRegenerationSpeed(player.getRegenerationSpeed() - activeBonus);
            activeBonus = 0;
            timeRemaining = 0;
        }
    }
}
