package ma.ac.emi.gamelogic.particle;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticleDefinition {
    private String id;
    private String spritePath;
    private int spriteWidth;
    private int spriteHeight;
    private double lifetime;
    private double spawnRate;
    
    private AnimationDetails animationDetails;

    public void applyDefaults() {
        if (lifetime <= 0) lifetime = 1.0;
    }
    
    
    public static class AnimationDetails{
    	public int initLength, loopLength, finishLength;
    	
    	public AnimationDetails(int initLength, int loopLength, int finishLength) {
    		this.initLength = initLength;
    		this.loopLength = loopLength;
    		this.finishLength = finishLength;
    	}
    	
    }
}

