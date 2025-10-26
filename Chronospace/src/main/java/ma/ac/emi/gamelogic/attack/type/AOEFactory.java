package ma.ac.emi.gamelogic.attack.type;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

public class AOEFactory {
	private static final Map<String, AOEType> types = new HashMap<>();

    public static AOEType getAOEType(String key, Image sprite, int boundWidth, int boundHeight, double effectRate, double ageMax) {
        AOEType type = types.get(key);
        if (type == null) {
            type = new AOEType(sprite, boundWidth, boundHeight, effectRate, ageMax);
            types.put(key, type);
        }
        return type;
    }
}
