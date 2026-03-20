package ma.ac.emi.gamelogic.weapon.behavior;

import com.google.gson.JsonObject;

import ma.ac.emi.gamelogic.weapon.behavior.passive.PassiveWeaponEffect;
import ma.ac.emi.gamelogic.weapon.behavior.passive.PassiveWeaponEffectDefinition;
import ma.ac.emi.gamelogic.weapon.behavior.passive.WeaponPassiveDefinition;

import java.util.HashMap;
import java.util.Map;

public class WeaponBehaviorFactory {
    public static WeaponBehaviorDefinition create(JsonObject json) {
        String type = json.get("type").getAsString();
        switch (type) {
            case "onAttackEffect" -> {
                return new OnAttackEffectBehaviorDefinition(
                        json.get("particleId").getAsString(),
                        json.get("offsetX").getAsDouble(),
                        json.get("offsetY").getAsDouble(),
                        json.get("count").getAsInt(),
                        json.get("radius").getAsDouble(),
                        json.get("emitterRadius").getAsDouble(),
                        json.get("ageMax").getAsDouble(),
                        json.get("isOneTime").getAsBoolean(),
                        json.get("aligned").getAsBoolean());
            }
            case "onSwitchInEffect" -> {
                return new OnSwitchInEffectBehaviorDefinition(
                        json.get("particleId").getAsString(),
                        json.get("offsetX").getAsDouble(),
                        json.get("offsetY").getAsDouble(),
                        json.get("count").getAsInt(),
                        json.get("radius").getAsDouble(),
                        json.get("emitterRadius").getAsDouble(),
                        json.get("ageMax").getAsDouble(),
                        json.get("isOneTime").getAsBoolean(),
                        json.get("aligned").getAsBoolean());
            }
            case "onSwitchOutEffect" -> {
                return new OnSwitchOutEffectBehaviorDefinition(
                        json.get("particleId").getAsString(),
                        json.get("offsetX").getAsDouble(),
                        json.get("offsetY").getAsDouble(),
                        json.get("count").getAsInt(),
                        json.get("radius").getAsDouble(),
                        json.get("emitterRadius").getAsDouble(),
                        json.get("ageMax").getAsDouble(),
                        json.get("isOneTime").getAsBoolean(),
                        json.get("aligned").getAsBoolean());
            }

            // ── Simple stat passive ────────────────────────────────────────
            case "passive" -> {
                String stat  = json.get("stat").getAsString();
                double value = json.get("value").getAsDouble();
                String opStr = json.has("operation")
                        ? json.get("operation").getAsString().toUpperCase() : "ADD";
                PassiveWeaponEffect.Operation op =
                        PassiveWeaponEffect.Operation.valueOf(opStr);
                return new PassiveWeaponEffectDefinition(stat, value, op);
            }

            // ── Complex parameterised passive ──────────────────────────────
            case "passive_complex" -> {
                String effect = json.get("effect").getAsString();
                Map<String, Double> params = new HashMap<>();
                if (json.has("params")) {
                    json.getAsJsonObject("params").entrySet()
                            .forEach(e -> params.put(e.getKey(),
                                    e.getValue().getAsDouble()));
                }
                return new WeaponPassiveDefinition(effect, params);
            }

            default -> throw new IllegalArgumentException("Unknown behavior type: " + type);
        }
    }
}