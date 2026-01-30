package ma.ac.emi.gamelogic.player;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ma.ac.emi.glgraphics.color.SpriteColorCorrection;
import ma.ac.emi.glgraphics.lighting.LightingStrategy;
import ma.ac.emi.glgraphics.post.config.PostProcessingDetails;
import ma.ac.emi.glgraphics.post.config.PostProcessingFactory;

import java.io.InputStreamReader;

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
        
        return config;
    }
}
