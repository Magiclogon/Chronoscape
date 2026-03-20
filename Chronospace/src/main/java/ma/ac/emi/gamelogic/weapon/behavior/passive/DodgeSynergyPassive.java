package ma.ac.emi.gamelogic.weapon.behavior.passive;

import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.weapon.Weapon;

/**
 * Dodge Synergy — while equipped, gain bonus speed proportional to dodge stat.
 * For every 1% dodge the player has, gain X% speed.
 * Recalculates every tick so it responds to dodge items bought mid-session.
 *
 * Designed for: Bow, Snipnk (kite builds that stack both)
 *
 * Params:
 *   speedPerDodgePct (default 0.5) — speed % gained per 1% dodge
 *                                    e.g. 0.5 means 20% dodge → +10% speed
 */
public class DodgeSynergyPassive extends WeaponPassive {

    private double speedPerDodgePct = 0.5;
    private double lastBonus        = 0;
    private boolean active          = false;

    @Override
    public void configure(java.util.Map<String, Double> p) {
        super.configure(p);
        speedPerDodgePct = param("speedPerDodgePct", 0.5);
    }

    @Override
    public void onSwitchIn(Weapon weapon) {
        active = true;
        lastBonus = 0;
    }

    @Override
    public void onSwitchOut(Weapon weapon) {
        Player player = player(weapon);
        if (player != null && lastBonus != 0)
            player.setSpeed(player.getSpeed() / (1.0 + lastBonus));
        lastBonus = 0;
        active = false;
    }

    @Override
    public void onUpdate(Weapon weapon, double step) {
        if (!active) return;
        Player player = player(weapon);
        if (player == null) return;

        double bonus = player.getDodge() * speedPerDodgePct;
        double delta = bonus - lastBonus;
        if (Math.abs(delta) > 0.0001) {
            player.setSpeed(player.getSpeed() * (1.0 + bonus) / (1.0 + lastBonus));
            lastBonus = bonus;
        }
    }
}
