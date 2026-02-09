package ma.ac.emi.gamelogic.attack.type;

import com.google.gson.*;
import ma.ac.emi.gamelogic.attack.behavior.Behavior;
import ma.ac.emi.gamelogic.attack.behavior.BehaviorFactory;
import ma.ac.emi.gamelogic.attack.behavior.definition.BehaviorDefinition;
import ma.ac.emi.glgraphics.color.SpriteColorCorrection;
import ma.ac.emi.glgraphics.lighting.LightingStrategy;
import ma.ac.emi.glgraphics.post.config.PostProcessingDetails;
import ma.ac.emi.glgraphics.post.config.PostProcessingFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ProjectileLoader {

    private final Map<String, ProjectileDefinition> projectileDefinitions = new HashMap<>();
    private static ProjectileLoader instance;

    private ProjectileLoader() {}

    public static ProjectileLoader getInstance() {
        if (instance == null) instance = new ProjectileLoader();
        return instance;
    }

    public void load(String filePath) {

        try (FileReader reader = new FileReader(filePath)) {

            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray projectiles = root.getAsJsonArray("projectiles");

            for (JsonElement element : projectiles) {

            	JsonObject node = element.getAsJsonObject();

                String id = node.get("id").getAsString();
                int width = node.get("width").getAsInt();
                int height = node.get("height").getAsInt();
                String spritePath = node.get("sprite").getAsString();

                List<BehaviorDefinition> behaviors = new ArrayList<>();

                if (node.has("behaviors")) {
                    JsonArray behaviorArray = node.getAsJsonArray("behaviors");
                    for (JsonElement b : behaviorArray) {
                    	JsonObject behaviorJson = b.getAsJsonObject();
                        behaviors.add(BehaviorFactory.create(behaviorJson));
                    }
                }

                ProjectileDefinition def = new ProjectileDefinition(
                        id,
                        spritePath,
                        width,
                        height,
                        behaviors
                );
                
                Gson gson = new Gson();
                
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

                projectileDefinitions.put(id, def);
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
