package ma.ac.emi.gamelogic.attack.type;

import java.awt.Image;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AOEDefinition extends AttackObjectType{
	private double effectRate;
	private double ageMax;
	private AnimationDetails animationDetails;

	public AOEDefinition(String id, AnimationDetails animationDetails, double effectRate, double ageMax) {
		super(id);
		this.animationDetails = animationDetails;
		this.effectRate = effectRate;
		this.ageMax = ageMax;
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
