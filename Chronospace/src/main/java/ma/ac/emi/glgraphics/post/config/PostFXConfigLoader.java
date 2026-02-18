package ma.ac.emi.glgraphics.post.config;

import java.io.InputStreamReader;
import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PostFXConfigLoader {
    
    private static final String RESOURCE_PATH = "/configs/postfx.json";
    private static final String EXTERNAL_CONFIG_PATH = "configs/postfx.json";
    

    public static PostFXConfig load() {
        // Try to load from external file first (user-modified config)
        File externalFile = new File(EXTERNAL_CONFIG_PATH);
        if (externalFile.exists()) {
            try (Reader reader = new FileReader(externalFile)) {
                return new Gson().fromJson(reader, PostFXConfig.class);
            } catch (Exception e) {
                System.err.println("Failed to load external config, falling back to resource: " + e.getMessage());
            }
        }
        
        // Fallback to resource (default config)
        try (Reader reader = new InputStreamReader(
                PostFXConfigLoader.class.getResourceAsStream(RESOURCE_PATH)
        )) {
            return new Gson().fromJson(reader, PostFXConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load postfx config", e);
        }
    }

    public static void save(PostFXConfig config) {
        try {
            // Create configs directory if it doesn't exist
            Path configDir = Paths.get("configs");
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
            }
            
            // Write JSON with pretty printing
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            
            String json = gson.toJson(config);
            
            // Write to file
            try (FileWriter writer = new FileWriter(EXTERNAL_CONFIG_PATH)) {
                writer.write(json);
            }
            
            System.out.println("Config saved to: " + EXTERNAL_CONFIG_PATH);
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to save postfx config", e);
        }
    }
 
    public static void resetToDefault() {
        File externalFile = new File(EXTERNAL_CONFIG_PATH);
        if (externalFile.exists()) {
            if (externalFile.delete()) {
                System.out.println("Config reset to default");
            } else {
                System.err.println("Failed to delete external config file");
            }
        }
    }
}