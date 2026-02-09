package ma.ac.emi.gamelogic.particle;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.glgraphics.color.SpriteColorCorrection;
import ma.ac.emi.glgraphics.entitypost.config.PostProcessingDetails;
import ma.ac.emi.glgraphics.lighting.LightingStrategy;

@Getter
@Setter
public class ParticleDefinition {
    private String id;
    private double lifetime;
    private double spawnRate;
    
    private AnimationDetails animationDetails;
    private transient SpriteColorCorrection colorCorrection;
    private transient LightingStrategy lightingStrategy;

    public void applyDefaults() {
        if (lifetime <= 0) lifetime = 1.0;
    }
    
    
    public static class AnimationDetails{
    	public String spritePath;
    	public int spriteWidth, spriteHeight;
    	public int initLength, loopLength, finishLength;
    	
    	public AnimationDetails(int initLength, int loopLength, int finishLength) {
    		this.initLength = initLength;
    		this.loopLength = loopLength;
    		this.finishLength = finishLength;
    	}
    	
    }
}

