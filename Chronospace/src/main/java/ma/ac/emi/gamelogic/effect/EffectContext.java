package ma.ac.emi.gamelogic.effect;

import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.weapon.Weapon;

public class EffectContext {

    private final java.util.List<PlayerEffect> effects = new java.util.ArrayList<>();

    // ── Registration ──────────────────────────────────────────────────────

    public void register(PlayerEffect effect, Player player) {
        effects.add(effect);
        effect.onRegister(player);
    }

    public void unregister(PlayerEffect effect, Player player) {
        if (effects.remove(effect))
            effect.onUnregister(player);
    }

    public void clear(Player player) {
        for (PlayerEffect e : effects) e.onUnregister(player);
        effects.clear();
    }

    // ── Fire hooks ────────────────────────────────────────────────────────

    public void fireOnTick(Player player, double step) {
        for (PlayerEffect e : effects) e.onTick(player, step);
    }

    public void fireOnKill(Player player, LivingEntity killed) {
        for (PlayerEffect e : effects) e.onKill(player, killed);
    }

    public double fireOnDamageTaken(Player player, double damage, LivingEntity attacker) {
        double d = damage;
        for (PlayerEffect e : effects) d = e.onDamageTaken(player, d, attacker);
        return d;
    }
    
    public double fireOnDamageTaken(Player player, double damage) {
        double d = damage;
        for (PlayerEffect e : effects) d = e.onDamageTaken(player, d);
        return d;
    }

    public void fireOnAttack(Player player, Weapon weapon) {
        for (PlayerEffect e : effects) e.onAttack(player, weapon);
    }

    public void fireOnDamageDealt(Player player, LivingEntity target, double damageDealt) {
        for (PlayerEffect e : effects) e.onDamageDealt(player, target, damageDealt);
    }
    
    public void fireOnDamageApplied(Player player) {
        for (PlayerEffect e : effects) e.onDamageApplied(player);
    }
    
    public void fireOnHeal(Player player, double requested, double gained) {
        for (PlayerEffect e : effects) e.onHeal(player, requested, gained);
    }
    
    public void fireOnDodge(Player player) {
        for (PlayerEffect e : effects) e.onDodge(player);
    }

    public void fireOnWaveStart(Player player) {
        for (PlayerEffect e : effects) e.onWaveStart(player);
    }

    public void fireOnWaveEnd(Player player) {
        for (PlayerEffect e : effects) e.onWaveEnd(player);
    }
}