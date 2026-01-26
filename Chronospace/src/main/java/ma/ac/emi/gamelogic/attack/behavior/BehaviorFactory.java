package ma.ac.emi.gamelogic.attack.behavior;

import com.google.gson.JsonObject;
import ma.ac.emi.gamelogic.attack.behavior.definition.*;

public class BehaviorFactory {

    public static BehaviorDefinition create(JsonObject json) {

        String type = json.get("type").getAsString();

        switch (type) {

            case "damage":
                return new DamageBehaviorDefinition(
                        json.get("destroyOnHit").getAsBoolean()
                );

            case "particle_trail":
                return new ParticleTrailBehaviorDefinition(
                        json.get("particleId").getAsString(),
                        json.get("emitterRadius").getAsDouble()
                );

            case "aoe_spawn":
                return new AOESpawnBehaviorDefinition(
                        json.get("aoeId").getAsString(),
                        json.get("count").getAsInt(),
                        json.get("radius").getAsDouble()
                );
            case "particle_spawn":
                return new ParticleSpawnBehaviorDefinition(
                        json.get("particleId").getAsString(),
                        json.get("count").getAsInt(),
                        json.get("radius").getAsDouble(),
                        json.get("emitterRadius").getAsDouble(),
                        json.get("ageMax").getAsDouble(),
                        json.get("isOneTime").getAsBoolean()
                );

            default:
                throw new IllegalArgumentException("Unknown behavior type: " + type);
        }
    }
}
