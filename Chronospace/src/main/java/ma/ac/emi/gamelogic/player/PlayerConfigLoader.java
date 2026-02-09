package ma.ac.emi.gamelogic.player;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ma.ac.emi.gamelogic.entity.behavior.EntityBehaviorDefinition;
import ma.ac.emi.gamelogic.entity.behavior.EntityBehaviorFactory;
import ma.ac.emi.glgraphics.color.SpriteColorCorrection;
import ma.ac.emi.glgraphics.entitypost.config.PostProcessingDetails;
import ma.ac.emi.glgraphics.entitypost.config.PostProcessingFactory;
import ma.ac.emi.glgraphics.lighting.LightingStrategy;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PlayerConfigLoader {

    public static PlayerConfig load(String path) {
        Gson gson = new Gson();
        InputStreamReader reader = new InputStreamReader(
                PlayerConfigLoader.class.getResourceAsStream(path)
        );
        JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
        PlayerConfig config = gson.fromJson(obj, PlayerConfig.class);
        
        if (obj.has("postProcessingDetails")) {
            JsonObject ppDetails = obj.getAsJsonObject("postProcessingDetails");
            PostProcessingDetails postProcessing = gson.fromJson(ppDetails, PostProcessingDetails.class);
            
            // Create ColorCorrection from config
            SpriteColorCorrection colorCorrection = PostProcessingFactory.createColorCorrection(postProcessing);
            config.colorCorrection = colorCorrection;
            
            // Create LightingStrategy from config
            LightingStrategy lightingStrategy = PostProcessingFactory.createLightingStrategy(postProcessing);
            config.lightingStrategy = lightingStrategy;
        }
        
        List<EntityBehaviorDefinition> behaviors = new ArrayList<>();
    	if (obj.has("behaviors")) {
            JsonArray behaviorArray = obj.getAsJsonArray("behaviors");
            for (JsonElement b : behaviorArray) {
            	JsonObject behaviorJson = b.getAsJsonObject();
                behaviors.add(EntityBehaviorFactory.create(behaviorJson));
            }
        }
    	
    	config.behaviorDefinitions = behaviors;
        
        return config;
    }
}
