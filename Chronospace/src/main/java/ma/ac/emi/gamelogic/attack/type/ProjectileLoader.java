package ma.ac.emi.gamelogic.attack.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProjectileLoader {
    private final Map<String, ProjectileDefinition> projectileDefinitions = new HashMap<>();
    private static ProjectileLoader instance;
    
    private ProjectileLoader() {}
    public static ProjectileLoader getInstance() {
    	if(instance == null) instance = new ProjectileLoader();
    	return instance;
    }

    public void load(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            
            // Load Single-Hit projectiles
            if (root.has("singleHit")) {
                JsonArray singleHitArray = root.getAsJsonArray("singleHit");
                for (JsonElement element : singleHitArray) {
                    JsonObject node = element.getAsJsonObject();
                    ProjectileSingleHitDefinition def = new ProjectileSingleHitDefinition(
                            node.get("id").getAsString(),
                            null,
                            node.get("speed").getAsFloat(),
                            node.get("width").getAsInt(),
                            node.get("height").getAsInt()
                    );
                    projectileDefinitions.put(def.getId(), def);
                }
            }
            
            // Load AOE projectiles
            if (root.has("aoe")) {
                JsonArray aoeArray = root.getAsJsonArray("aoe");
                for (JsonElement element : aoeArray) {
                    JsonObject node = element.getAsJsonObject();
                    ProjectileAOEDefinition def = new ProjectileAOEDefinition(
                            node.get("id").getAsString(),
                            null,
                            node.get("speed").getAsFloat(),
                            node.get("width").getAsInt(),
                            node.get("height").getAsInt(),
                            node.has("aoeId") ? node.get("aoeId").getAsString() : null
                    );
                    projectileDefinitions.put(def.getId(), def);
                }
            }
            
            System.out.println("Loaded " + projectileDefinitions.size() + " projectiles");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ProjectileDefinition get(String id) {
        return projectileDefinitions.get(id);
    }

    public Map<String, ProjectileDefinition> getAll() {
        return projectileDefinitions;
    }
}