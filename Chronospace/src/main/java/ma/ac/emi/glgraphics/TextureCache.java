package ma.ac.emi.glgraphics;

import com.jogamp.opengl.GL3;
import java.util.HashMap;
import java.util.Map;

public class TextureCache {

    private static final Map<Object, Texture> cache = new HashMap<>();

    public static Texture get(GL3 gl, Object key, TextureLoader loader) {
        return cache.computeIfAbsent(key, k -> loader.load(gl));
    }

    @FunctionalInterface
    public interface TextureLoader {
        Texture load(GL3 gl);
    }
}
