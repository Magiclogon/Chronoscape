package ma.ac.emi.gamelogic.particle;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ma.ac.emi.glgraphics.color.SpriteColorCorrection;
import ma.ac.emi.glgraphics.lighting.LightingStrategy;
import ma.ac.emi.glgraphics.post.config.PostProcessingDetails;
import ma.ac.emi.glgraphics.post.config.PostProcessingFactory;

public class ParticleLoader {
	
	public void loadFromJson(String filePath, Map<String, ParticleDefinition> definitions, Map<String, Double> lastSpawnTimes) {
        try (Reader reader = new FileReader(filePath)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray arr = root.getAsJsonArray("effects");
            Gson gson = new Gson();

            for (JsonElement e : arr) {
            	JsonObject obj = e.getAsJsonObject();
            	ParticleDefinition def = gson.fromJson(obj, ParticleDefinition.class);
                def.applyDefaults();
                
             // Parse and apply post-processing if present
	            if (obj.has("postProcessingDetails")) {
	                JsonObject ppDetails = obj.getAsJsonObject("postProcessingDetails");
	                PostProcessingDetails postProcessing = gson.fromJson(ppDetails, PostProcessingDetails.class);
	                
	                // Create ColorCorrection from config
	                SpriteColorCorrection colorCorrection = PostProcessingFactory.createColorCorrection(postProcessing);
	                def.setColorCorrection(colorCorrection);
	                
	                // Create LightingStrategy from config
	                LightingStrategy lightingStrategy = PostProcessingFactory.createLightingStrategy(postProcessing);
	                def.setLightingStrategy(lightingStrategy);
	            }
                
                definitions.put(def.getId(), def);
                lastSpawnTimes.put(def.getId(), -999.0);
                
                // Pre-load animation into cache
            }
  
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
