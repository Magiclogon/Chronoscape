package ma.ac.emi.gamelogic.particle;

import com.jogamp.opengl.GL3;
import ma.ac.emi.fx.Sprite;
import ma.ac.emi.glgraphics.Texture;

public class ParticleAnimation {
    public final Sprite[] initFrames;
    public final Sprite[] loopFrames;
    public final Sprite[] finishFrames;
    
    // Cached GL textures
    private Texture[] initTextures;
    private Texture[] loopTextures;
    private Texture[] finishTextures;
    
    public ParticleAnimation(Sprite[] initFrames, Sprite[] loopFrames, Sprite[] finishFrames) {
        this.initFrames = initFrames;
        this.loopFrames = loopFrames;
        this.finishFrames = finishFrames;
    }
    
    /**
     * Initialize all textures at once (call this once during loading)
     */
    public void initializeTextures(GL3 gl) {
        if (initTextures == null) {
            initTextures = new Texture[initFrames.length];
            for (int i = 0; i < initFrames.length; i++) {
                initTextures[i] = initFrames[i].getTexture(gl);
            }
        }
        
        if (loopTextures == null) {
            loopTextures = new Texture[loopFrames.length];
            for (int i = 0; i < loopFrames.length; i++) {
                loopTextures[i] = loopFrames[i].getTexture(gl);
            }
        }
        
        if (finishTextures == null) {
            finishTextures = new Texture[finishFrames.length];
            for (int i = 0; i < finishFrames.length; i++) {
                finishTextures[i] = finishFrames[i].getTexture(gl);
            }
        }
    }
    
    public Texture getTexture(ParticlePhase phase, int frameIndex) {
        return switch (phase) {
            case INIT -> initTextures[Math.min(frameIndex, initTextures.length - 1)];
            case LOOP -> loopTextures[frameIndex % loopTextures.length];
            case FINISH -> finishTextures[Math.min(frameIndex, finishTextures.length - 1)];
        };
    }
    
    public Sprite getSprite(ParticlePhase phase, int frameIndex) {
        return switch (phase) {
            case INIT -> initFrames[Math.min(frameIndex, initFrames.length - 1)];
            case LOOP -> loopFrames[frameIndex % loopFrames.length];
            case FINISH -> finishFrames[Math.min(frameIndex, finishFrames.length - 1)];
        };
    }
}