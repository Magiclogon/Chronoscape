package ma.ac.emi.gamelogic.entity.behavior;

import com.google.gson.JsonObject;

public class EntityBehaviorFactory {
	public static EntityBehaviorDefinition create(JsonObject json) {

        String type = json.get("type").getAsString();

        switch (type) {
	        case "onDeath":
	        	return new OnDeathEffectBehaviorDefinition(
	                        json.get("particleId").getAsString(),
	                        json.get("count").getAsInt(),
	                        json.get("radius").getAsDouble(),
	                        json.get("emitterRadius").getAsDouble(),
	                        json.get("ageMax").getAsDouble(),
	                        json.get("isOneTime").getAsBoolean()
	                );
	        case "onHit":
	        	return new OnHitEffectBehaviorDefinition(
	        			json.get("particleId").getAsString(),
	        			json.get("count").getAsInt(),
	        			json.get("radius").getAsDouble(),
	        			json.get("emitterRadius").getAsDouble(),
	        			json.get("ageMax").getAsDouble(),
	        			json.get("isOneTime").getAsBoolean()
	        			);
	        case "onSpawn":
	        	return new OnSpawnEffectBehaviorDefinition(
	        			json.get("particleId").getAsString(),
	        			json.get("count").getAsInt(),
	        			json.get("radius").getAsDouble(),
	        			json.get("emitterRadius").getAsDouble(),
	        			json.get("ageMax").getAsDouble(),
	        			json.get("isOneTime").getAsBoolean()
	        			);
	        case "onMovement":
	        	return new OnMovementEffectBehaviorDefinition(
	                    json.get("particleId").getAsString(),
	                    json.get("emitterRadius").getAsDouble(),
	                    json.get("offsetX").getAsDouble(),
	                    json.get("offsetY").getAsDouble()
	            );
	        	
	        case "onHitFlash":
	        	return new OnHitFlashBehaviorDefinition(
	        			json.get("duration").getAsDouble(),
	        			json.get("intensity").getAsDouble()
	        	);
	        	
	        case "onHitInvincible":
	        	return new OnHitInvincibilityBehaviorDefinition(
	        			json.get("duration").getAsDouble(),
	        			json.get("flashingFrequency").getAsDouble()
    			);
            
            default:
                throw new IllegalArgumentException("Unknown behavior type: " + type);
        }
    }
}
