package ma.ac.emi.gamelogic.particle;

import com.jogamp.opengl.GL3;
import ma.ac.emi.fx.Sprite;
import ma.ac.emi.glgraphics.Texture;

public class ParticleAnimation {
    public final Sprite[] initFrames;
    public final Sprite[] loopFrames;
    public final Sprite[] finishFrames;
    
    private Texture[] initTextures;
    private Texture[] loopTextures;
    private Texture[] finishTextures;
    private volatile boolean texturesInitialized = false;
    
    public ParticleAnimation(Sprite[] initFrames, Sprite[] loopFrames, Sprite[] finishFrames) {
        this.initFrames = initFrames;
        this.loopFrames = loopFrames;
        this.finishFrames = finishFrames;
    }
    
    public void initializeTextures(GL3 gl) {
        if (texturesInitialized) return;
        
        initTextures = new Texture[initFrames.length];
        for (int i = 0; i < initFrames.length; i++) {
            initTextures[i] = initFrames[i].getTexture(gl);
        }
        
        loopTextures = new Texture[loopFrames.length];
        for (int i = 0; i < loopFrames.length; i++) {
            loopTextures[i] = loopFrames[i].getTexture(gl);
        }
        
        finishTextures = new Texture[finishFrames.length];
        for (int i = 0; i < finishFrames.length; i++) {
            finishTextures[i] = finishFrames[i].getTexture(gl);
        }
        
        texturesInitialized = true;
    }
    
    public Texture getTexture(ParticlePhase phase, int frameIndex) {
        // Safety check - if textures not initialized, return null
        if (!texturesInitialized) {
            return null;
        }
        
        return switch (phase) {
            case INIT -> initTextures != null && initTextures.length > 0 ? 
                        initTextures[Math.min(frameIndex, initTextures.length - 1)] : null;
            case LOOP -> loopTextures != null && loopTextures.length > 0 ? 
                        loopTextures[frameIndex % loopTextures.length] : null;
            case FINISH -> finishTextures != null && finishTextures.length > 0 ? 
                          finishTextures[Math.min(frameIndex, finishTextures.length - 1)] : null;
        };
    }
    
    public Sprite getSprite(ParticlePhase phase, int frameIndex) {
        return switch (phase) {
            case INIT -> initFrames[Math.min(frameIndex, initFrames.length - 1)];
            case LOOP -> loopFrames[frameIndex % loopFrames.length];
            case FINISH -> finishFrames[Math.min(frameIndex, finishFrames.length - 1)];
        };
    }
    
    public boolean areTexturesInitialized() {
        return texturesInitialized;
    }
    
    /**
     * Dispose of all textures
     */
    public void dispose(GL3 gl) {
        if (initTextures != null) {
            for (Texture tex : initTextures) {
                if (tex != null) tex.dispose(gl);
            }
            initTextures = null;
        }
        
        if (loopTextures != null) {
            for (Texture tex : loopTextures) {
                if (tex != null) tex.dispose(gl);
            }
            loopTextures = null;
        }
        
        if (finishTextures != null) {
            for (Texture tex : finishTextures) {
                if (tex != null) tex.dispose(gl);
            }
            finishTextures = null;
        }
        
        texturesInitialized = false;
    }
}