package ma.ac.emi.gamelogic.factory;

import lombok.Getter;

@Getter
public abstract class EnemyDefinition {
	protected final double speed;
	protected final double hpMax;
	protected final String weaponId;
	
	protected final AnimationDetails animationDetails;
	
	public EnemyDefinition(double speed, double hpMax, String weaponId, 
			AnimationDetails animationDetails) {
		this.speed = speed;
		this.hpMax = hpMax;
		this.weaponId = weaponId;
		
		this.animationDetails = animationDetails;
	}
	
	public static class AnimationDetails{
		public String spriteSheetPath;
		public int spriteWidth, spriteHeight;
		public int idleLength, runningLength, backingLength, dyingLength;
		
	}

}

