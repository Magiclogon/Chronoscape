package ma.ac.emi.gamelogic.attack.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AOELoader {
    private final Map<String, AOEDefinition> aoeDefinitions = new HashMap<>();
    private static AOELoader instance;
    
    private AOELoader() {}
    public static AOELoader getInstance() {
    	if(instance == null) instance = new AOELoader();
    	return instance;
    }

    public void load(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            
            // Load aoes
            if (root.has("aoes")) {
                JsonArray aoesArray = root.getAsJsonArray("aoes");
                for (JsonElement element : aoesArray) {
                    JsonObject node = element.getAsJsonObject();
                    AOEDefinition def = new AOEDefinition(
                    		node.get("id").getAsString(),
                    		null,
                    		node.get("boundWidth").getAsInt(),
                    		node.get("boundHeight").getAsInt(),
                    		node.get("effectRate").getAsDouble(),
                    		node.get("ageMax").getAsDouble()
                		);
                    aoeDefinitions.put(def.getId(), def);
                }
            }
            
            System.out.println("Loaded " + aoeDefinitions.size() + " aoes");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AOEDefinition get(String id) {
        return aoeDefinitions.get(id);
    }

    public Map<String, AOEDefinition> getAll() {
        return aoeDefinitions;
    }
}