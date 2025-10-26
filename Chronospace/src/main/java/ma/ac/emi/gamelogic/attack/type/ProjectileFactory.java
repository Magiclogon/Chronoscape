package ma.ac.emi.gamelogic.attack.type;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

public class ProjectileFactory{
    private static final Map<String, ProjectileType> types = new HashMap<>();

    public static ProjectileType getProjectileType(String key, Image sprite, double baseSpeed, int boundWidth, int boundHeight) {
        ProjectileType type = types.get(key);
        if (type == null) {
            type = new ProjectileType(sprite, baseSpeed, boundWidth, boundHeight);
            types.put(key, type);
        }
        return type;
    }


}
