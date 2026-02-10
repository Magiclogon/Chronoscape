package ma.ac.emi.glgraphics.post.config;

import java.io.InputStreamReader;
import java.io.Reader;

import com.google.gson.Gson;

public class PostFXConfigLoader {

    public static PostFXConfig load() {
        try (Reader reader = new InputStreamReader(
                PostFXConfigLoader.class
                        .getResourceAsStream("/configs/postfx.json")
        )) {
            return new Gson().fromJson(reader, PostFXConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load postfx config", e);
        }
    }
}
