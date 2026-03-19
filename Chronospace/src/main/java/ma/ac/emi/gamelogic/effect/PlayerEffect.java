package ma.ac.emi.gamelogic.effect;

import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.weapon.Weapon;


public interface PlayerEffect {
	
	default void configure(java.util.Map<String, Double> params) {}
	 
    /** Reads a double param by name, returning defaultValue if absent. */
    static double param(java.util.Map<String, Double> params, String key, double defaultValue) {
        if (params == null) return defaultValue;
        Double v = params.get(key);
        return v != null ? v : defaultValue;
    }
 
    /** Reads an int param by name, returning defaultValue if absent. */
    static int paramInt(java.util.Map<String, Double> params, String key, int defaultValue) {
        if (params == null) return defaultValue;
        Double v = params.get(key);
        return v != null ? v.intValue() : defaultValue;
    }
   
    default void onRegister(Player player) {}

    default void onUnregister(Player player) {}

    default void onTick(Player player, double step) {}
  
    default void onKill(Player player, LivingEntity killed) {}

    default double onDamageTaken(Player player, double damage) { return damage; }
 
    default void onAttack(Player player, Weapon weapon) {}

    default void onDamageDealt(Player player, LivingEntity target, double damageDealt) {}
    
    default void onDodge(Player player) {}

    default void onWaveStart(Player player) {}

    default void onWaveEnd(Player player) {}
}