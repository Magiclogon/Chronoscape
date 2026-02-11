package ma.ac.emi.gamelogic.weapon.behavior;

import com.google.gson.JsonObject;

public class WeaponBehaviorFactory {
	public static WeaponBehaviorDefinition create(JsonObject json) {

        String type = json.get("type").getAsString();

        switch (type) {
	        case "onAttackEffect":
	        	return new OnAttackEffectBehaviorDefinition(
	        			json.get("particleId").getAsString(),
	        			json.get("offsetX").getAsDouble(),
	        			json.get("offsetY").getAsDouble(),
	        			json.get("count").getAsInt(),
	        			json.get("radius").getAsDouble(),
	        			json.get("emitterRadius").getAsDouble(),
	        			json.get("ageMax").getAsDouble(),
	        			json.get("isOneTime").getAsBoolean(),
	        			json.get("aligned").getAsBoolean()
	        			);
            default:
                throw new IllegalArgumentException("Unknown behavior type: " + type);
        }
    }
}
