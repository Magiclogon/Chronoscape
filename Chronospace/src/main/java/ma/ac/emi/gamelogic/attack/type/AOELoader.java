package ma.ac.emi.gamelogic.attack.type;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ma.ac.emi.glgraphics.color.SpriteColorCorrection;
import ma.ac.emi.glgraphics.entitypost.config.PostProcessingDetails;
import ma.ac.emi.glgraphics.entitypost.config.PostProcessingFactory;
import ma.ac.emi.glgraphics.lighting.LightingStrategy;

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
            Gson gson = new Gson();

            if (root.has("aoes")) {
                JsonArray aoesArray = root.getAsJsonArray("aoes");
                for (JsonElement element : aoesArray) {
                    JsonObject node = element.getAsJsonObject();
                    AOEDefinition def = gson.fromJson(node, AOEDefinition.class);
                                        
                    if (node.has("postProcessingDetails")) {
    	                JsonObject ppDetails = node.getAsJsonObject("postProcessingDetails");
    	                PostProcessingDetails postProcessing = gson.fromJson(ppDetails, PostProcessingDetails.class);
    	                
    	                // Create ColorCorrection from config
    	                SpriteColorCorrection colorCorrection = PostProcessingFactory.createColorCorrection(postProcessing);
    	                def.setColorCorrection(colorCorrection);
    	                
    	                // Create LightingStrategy from config
    	                LightingStrategy lightingStrategy = PostProcessingFactory.createLightingStrategy(postProcessing);
    	                def.setLightingStrategy(lightingStrategy);
    	            }
                    
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