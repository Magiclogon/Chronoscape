package ma.ac.emi.gamelogic.weapon;

import java.awt.Image;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ma.ac.emi.gamelogic.attack.type.AOEFactory;
import ma.ac.emi.gamelogic.attack.type.ProjectileFactory;

public class WeaponFactory {
	private static final Map<String, AttackStrategy> STRATEGIES = new HashMap<>();
    private static Map<String, JsonObject> weaponDefinitions = new HashMap<>();
    private static boolean initialized = false;
    
    static {
        STRATEGIES.put("RangeSingleHit", new RangeSingleHitStrategy());
        STRATEGIES.put("MeleeSingleHit", new MeleeSingleHitStrategy());
        STRATEGIES.put("RangeAOE", new RangeAOEStrategy());
        STRATEGIES.put("MeleeAOE", new MeleeAOEStrategy());
    }
    
    
    
 // Initialize weapon definitions from JSON file
    public static void initialize(String jsonFilePath) {
        if (initialized) return;
        
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            JsonObject root = JsonParser.parseString(jsonContent).getAsJsonObject();
            JsonArray weapons = root.getAsJsonArray("weapons");
            
            for (JsonElement element : weapons) {
                JsonObject weaponData = element.getAsJsonObject();
                String id = weaponData.get("id").getAsString();
                weaponDefinitions.put(id, weaponData);
            }
            
            initialized = true;
            System.out.println("Loaded " + weaponDefinitions.size() + " weapon definitions");
        } catch (Exception e) {
            System.err.println("Failed to load weapon definitions: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Create weapon by ID
    public static Weapon createWeapon(String weaponId) {
        if (!initialized) {
            throw new IllegalStateException("WeaponFactory not initialized. Call initialize() first.");
        }
        
        JsonObject weaponData = weaponDefinitions.get(weaponId);
        if (weaponData == null) {
            throw new IllegalArgumentException("Weapon with id '" + weaponId + "' not found");
        }
        
        return createFromJson(weaponData);
    }
    
    private static Weapon createFromJson(JsonObject weaponData) {
        Weapon weapon = new Weapon();
        
        // Load strategy
        String attackType = weaponData.get("attackType").getAsString();
        weapon.setAttackStrategy(STRATEGIES.get(attackType));
        
        weapon.setName(weaponData.get("name").getAsString());
        // Load stats
        weapon.setDamage(weaponData.get("damage").getAsDouble());
        weapon.setRange(weaponData.get("range").getAsDouble());
        weapon.setAttackSpeed(weaponData.get("attackSpeed").getAsDouble());
        weapon.setMagazineSize(weaponData.get("magazineSize").getAsInt());
        weapon.setAmmo(weapon.getMagazineSize());
        weapon.setReloadingTime(weaponData.get("reloadingTime").getAsDouble());
        
        // Load projectile type (if any)
        if (weaponData.has("projectileType")) {
            String projType = weaponData.get("projectileType").getAsString();
            weapon.setProjectileType(ProjectileFactory.getProjectileType(
                projType,
                null,
                weaponData.get("projectileSpeed").getAsDouble(),
                weaponData.get("projectileWidth").getAsInt(),
                weaponData.get("projectileHeight").getAsInt()
            ));
        }

        // Load AOE type (if any)
        if (weaponData.has("aoeType")) {
            String aoeTypeKey = weaponData.get("aoeType").getAsString();

            // Load AOE-specific parameters
            Image aoeSprite = null;
            int boundWidth = weaponData.get("aoeBoundWidth").getAsInt();
            int boundHeight = weaponData.get("aoeBoundHeight").getAsInt();
            double effectRate = weaponData.get("aoeEffectRate").getAsDouble();
            double ageMax = weaponData.get("aoeAgeMax").getAsDouble();

            weapon.setAoeType(AOEFactory.getAOEType(
                aoeTypeKey,
                aoeSprite,
                boundWidth,
                boundHeight,
                effectRate,
                ageMax
            ));
        }

        return weapon;
    }

    
    // Utility method to get all available weapon IDs
    public static Set<String> getAvailableWeaponIds() {
        return new HashSet<>(weaponDefinitions.keySet());
    }
}