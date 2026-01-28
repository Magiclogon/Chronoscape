package ma.ac.emi.gamelogic.particle;

import com.jogamp.opengl.GL3;
import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.fx.SpriteSheet;

import java.util.HashMap;
import java.util.Map;

public class ParticleAnimationCache {

    private static final Map<String, ParticleAnimation> CACHE = new HashMap<>();
    private static boolean texturesInitialized = false;

    public static ParticleAnimation get(ParticleDefinition def) {
        return CACHE.computeIfAbsent(def.getAnimationDetails().spritePath, key -> {
            SpriteSheet sheet = new SpriteSheet(
                AssetsLoader.getSprite(def.getAnimationDetails().spritePath),
                def.getAnimationDetails().spriteWidth,
                def.getAnimationDetails().spriteHeight
            );
            return new ParticleAnimation(
                sheet.getAnimationRow(0, def.getAnimationDetails().initLength),
                sheet.getAnimationRow(1, def.getAnimationDetails().loopLength),
                sheet.getAnimationRow(2, def.getAnimationDetails().finishLength)
            );
        });
    }
    
    public static void initializeAllTextures(GL3 gl) {
        if (texturesInitialized) return;
        
        for (ParticleAnimation anim : CACHE.values()) {
            anim.initializeTextures(gl);
        }
        
        texturesInitialized = true;
        System.out.println("Initialized " + CACHE.size() + " particle animation textures");
    }
    

    public static void clear(GL3 gl) {
        for (ParticleAnimation anim : CACHE.values()) {
            // Dispose textures if needed
        }
        CACHE.clear();
        texturesInitialized = false;
    }
}